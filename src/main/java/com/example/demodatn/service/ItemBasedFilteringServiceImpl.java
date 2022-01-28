package com.example.demodatn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demodatn.constant.Error;
import com.example.demodatn.constant.FavouriteType;
import com.example.demodatn.constant.MetadataType;
import com.example.demodatn.domain.FoodDomain;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemBasedFilteringServiceImpl {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private CalculateDistanceUtils calculateDistanceUtils;

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;


    public Map<Long, HashMap<Long, Double>> initializeData() {// user - item
        Map<Long, HashMap<Long, Double>> data = new HashMap<>();
        List<UserAppEntity> listUserApp = userAppRepository.getListUserActive();

        for (UserAppEntity userAppEntity : listUserApp){
            List<RatingEntity> listRatingOfUser = ratingRepository.findAllByUserAppId(userAppEntity.getId());
            if (!CollectionUtils.isEmpty(listRatingOfUser)){
                HashMap<Long, Double> listItemOfUser = new HashMap<>();
                for (RatingEntity ratingEntity : listRatingOfUser){
                    listItemOfUser.put(ratingEntity.getFoodId(), ratingEntity.getRating().doubleValue());
                }
                data.put(userAppEntity.getId(), listItemOfUser);
            }
        }
        for (Map.Entry<Long, HashMap<Long, Double>> e : data.entrySet()){
            System.out.println("User : " + e.getKey() + " , <item, double> = " + e.getValue().entrySet());
        }

        return data;
    }

//    @Scheduled(fixedRate = 372800000)
    public void setSummaryRating(){
        List<FoodEntity> listFood = foodRepository.findAll();
        List<FoodEntity> listResultFood = new ArrayList<>();
        for (FoodEntity foodEntity : listFood) {
            List<RatingEntity> listRatingOfFood = ratingRepository.findAllByFoodId(foodEntity.getId());
            if (!CollectionUtils.isEmpty(listRatingOfFood)) {
                Double totalRating = listRatingOfFood.stream().map(t -> t.getRating()).reduce(0l, (t1, t2) -> t1 + t2).doubleValue()/ listRatingOfFood.size();
                foodEntity.setSummaryRating(Math.round(totalRating*1000.0)/1000.0);
                listResultFood.add(foodEntity);
            }
        }
        foodRepository.saveAll(listResultFood);
        List<StoreEntity> listStore = storeRepository.findAll();
        List<StoreEntity> listResultStore = new ArrayList<>();
        for (StoreEntity store : listStore){
            List<FoodEntity> listFoodOfStore = foodRepository.findAllByStoreId(store.getId());
            List<FoodEntity> listFoodHaveRating = listFoodOfStore.stream().filter(t -> t.getSummaryRating() != null).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(listFoodHaveRating)){
                Double sumRating = listFoodHaveRating.stream().map(t -> t.getSummaryRating()).reduce(0.0, (t1, t2) -> t1 + t2)/listFoodHaveRating.size();
                store.setSummaryRating(Math.round(sumRating*1000.0)/1000.0);
                listResultStore.add(store);
                System.out.println("Store : " + store.getId());
                listFoodHaveRating.forEach(t -> System.out.println("Food : " + t.getId() + ", rating : " + t.getSummaryRating()));
            }

        }
        storeRepository.saveAll(listResultStore);


    }

//    @Scheduled(fixedRate = 172800000)
    public void setStoreIdForTransaction(){
        List<TransactionEntity> listTransaction = transactionRepository.findAll();
        List<TransactionEntity> lisResult = new ArrayList<>();
        for (TransactionEntity entity : listTransaction){
            System.out.println("Transaction :" + entity.getId());
            TransactionItemEntity transactionItemEntity = transactionItemRepository.findAllByTransactionId(entity.getId()).stream().limit(1).collect(Collectors.toList()).get(0);
            FoodEntity foodEntity = foodRepository.findById(transactionItemEntity.getFoodId()).orElse(null);
            if (foodEntity == null){
                throw new CustomException("Food bi sai", "Food bi sai", HttpStatus.BAD_REQUEST);
            }
            entity.setStoreId(foodEntity.getStoreId());
            lisResult.add(entity);
        }
        transactionRepository.saveAll(lisResult);
    }

//    @Scheduled(fixedRate = 372800000)
    public void buildDifferencesMatrixAndPredict() {
        Map<Long, HashMap<Long, Double>> data = initializeData();
        Map<Long, Map<Long, Double>> diff = new HashMap<>();
        Map<Long, Map<Long, Integer>> freq = new HashMap<>();
        Map<Long, HashMap<Long, Double>> outputData = new HashMap<>();

        for (HashMap<Long, Double> user : data.values()) {
            // user :  <item, double> = [Item(itemName=Drink)=0.4512057408205852, Item(itemName=Candy)=0.06152167998373126]
            for (Map.Entry<Long, Double> e : user.entrySet()) {
                // e :  Item(itemName=Drink)=0.4512057408205852
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Long, Double>());
                    freq.put(e.getKey(), new HashMap<Long, Integer>());
                }
                for (Map.Entry<Long, Double> e2 : user.entrySet()) {
                    // e2 : Item(itemName=Candy)=0.06152167998373126]b
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Long j : diff.keySet()) {//j : item
            for (Long i : diff.get(j).keySet()) {// i : item
                double oldValue = diff.get(j).get(i).doubleValue();
                int count = freq.get(j).get(i).intValue();
                diff.get(j).put(i, oldValue / count);
            }
        }

        //================================================================================

        HashMap<Long, Double> uPred = new HashMap<Long, Double>();
        HashMap<Long, Integer> uFreq = new HashMap<Long, Integer>();
        for (Long j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Map.Entry<Long, HashMap<Long, Double>> e : data.entrySet()) {
            for (Long j : e.getValue().keySet()) {
                for (Long k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j).doubleValue() + e.getValue().get(j).doubleValue();
                        double finalValue = predictedValue * freq.get(k).get(j).intValue();
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j).intValue());
                    } catch (NullPointerException e1) {
                    }
                }
            }
            HashMap<Long, Double> clean = new HashMap<Long, Double>();
            for (Long j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j).doubleValue() / uFreq.get(j).intValue());
                }
            }

            //======================================
            //get all food
            List<Long> listFoodIds = foodRepository.findAll().stream().map(t -> t.getId()).collect(Collectors.toList());

            for (Long j : listFoodIds) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
        Map<Long, LinkedHashMap<Long, Double>> orderedResult = new HashMap<>();
        for (Map.Entry<Long, HashMap<Long, Double>> e : outputData.entrySet()){
            List<Map.Entry<Long, Double>> list = new ArrayList<>(e.getValue().entrySet());
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            LinkedHashMap<Long, Double> result = new LinkedHashMap<>();
            for (Map.Entry<Long, Double> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            orderedResult.put(e.getKey(), result);
        }
//        printData1(orderedResult);
        writeDataToFile(orderedResult);
    }

    public void writeDataToFile(Map<Long, LinkedHashMap<Long, Double>> data){
        File file = new File("data.csv");
        try {
            FileWriter outputfile = new FileWriter(file);

            CSVWriter writer = new CSVWriter(outputfile, '|',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            List<String[]> result = new ArrayList<String[]>();
            for (Map.Entry<Long, LinkedHashMap<Long, Double>> e : data.entrySet()){
                List<String> lineList = new ArrayList<>();
                lineList.add("User" + e.getKey());
                for (Map.Entry<Long, Double> item : e.getValue().entrySet()){
                    lineList.add(StringUtils.convertObjectToString(item.getKey()));
                }
                String[] lineToWrite = lineList.toArray(new String[0]);
                result.add(lineToWrite);
            }
            writer.writeAll(result);
            writer.close();

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "djifhw3lo",
                    "api_key", "992726224781494",
                    "api_secret", "Tol4roEhAhgOJ3NaNsnAyWDDrD0",
                    "secure", true));
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "auto","folder", "data-recommend/"));
            String uploadResultUrl = (String) uploadResult.get("url");
            MetadataEntity metadataEntity = metadataRepository.findByIdAndType(1l, MetadataType.DATA_RECOMMEND_FILE.getValue());
            metadataEntity.setValue(uploadResultUrl);
            metadataRepository.save(metadataEntity);
            System.out.println(uploadResultUrl);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<FoodDomain> handleFileCsv(String userApp){
        List<FoodDomain> result = new ArrayList<>();
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Map<Long, Double> mapDistance = calculateDistanceUtils.getDistanceOfAllStores(userAppEntity);
        try {
            String linkData = metadataRepository.findByIdAndType(1l, MetadataType.DATA_RECOMMEND_FILE.getValue()).getValue();
            URL url = new URL(linkData);
            Scanner s = new Scanner(url.openStream());
            List<String> listLine = new ArrayList<>();
            while (s.hasNextLine()){
                listLine.add(s.nextLine());
            }

            for (String line : listLine){
                String[] listItem =line.split("\\|");
                Long userId = StringUtils.convertObjectToLongOrNull(listItem[0].substring(4));
                if (userAppId.equals(userId)){
                    List<String> listFoodStr = new ArrayList<>(Arrays.asList(listItem));
                    listFoodStr.remove(0);
                    List<Long> listFoodId = listFoodStr.stream().map(t -> StringUtils.convertStringToLongOrNull(t)).collect(Collectors.toList());
                    List<Long> listRatingFoodOfUser = ratingRepository.findAllByUserAppId(userAppId)
                                                            .stream().map(t -> t.getFoodId())
                                                            .distinct()
                                                            .collect(Collectors.toList());
                    List<Long> listWillShowFoodId = listFoodId.stream().filter(t -> !listRatingFoodOfUser.contains(t))
                                                            .limit(16)
                                                            .collect(Collectors.toList());

                    //fill missing number
                    if (listWillShowFoodId.size() < 16){
                        while (listWillShowFoodId.size() < 16){
                            for (Long foodId : listFoodId){
                                if (!listWillShowFoodId.contains(foodId)){
                                    listWillShowFoodId.add(foodId);
                                }
                            }
                        }
                    }

                    if (!CollectionUtils.isEmpty(listWillShowFoodId)){
                        for (Long foodId : listWillShowFoodId){
                            FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
                            if (foodEntity == null){
                                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                            }
                            FoodDomain foodDomain = new FoodDomain();
                            foodDomain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                            foodDomain.setName(foodEntity.getName());
                            foodDomain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
                            foodDomain.setStoreId(StringUtils.convertObjectToString(foodEntity.getStoreId()));
                            StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
                            if (storeEntity == null){
                                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                            }
                            foodDomain.setStoreName(storeEntity.getName());
                            foodDomain.setSummaryRating(StringUtils.convertObjectToString(foodEntity.getSummaryRating()));
                            foodDomain.setAvatar(foodEntity.getAvatar());
                            foodDomain.setPrice(foodEntity.getPrice().toString());
                            FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodEntity.getId(), FavouriteType.FOOD.getValue());
                            if (favouriteEntity != null){
                                foodDomain.setIsFavourite("1");
                            } else {
                                foodDomain.setIsFavourite("0");
                            }

                            foodDomain.setDiscountPercent(foodEntity.getDiscountPercent());
                            foodDomain.setOriginalPrice(foodEntity.getOriginalPrice());
                            foodDomain.setDistance(mapDistance.get(storeEntity.getId()));
                            result.add(foodDomain);
                        }
                    }
                }
            }
        return result;
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static void printData(Map<Long, HashMap<Long, Double>> data) {
        for (Long user : data.keySet()) {
            System.out.println("User :"+ user + ":");
            print(data.get(user));
        }
    }

    private static void printData1(Map<Long, LinkedHashMap<Long, Double>> data) {
        for (Long user : data.keySet()) {
            System.out.println("User :"+ user + ":");
            print1(data.get(user));
        }
    }

    private static void print(HashMap<Long, Double> hashMap) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        for (Long j : hashMap.keySet()) {
            System.out.println("Item " + j + " --> " + formatter.format(hashMap.get(j).doubleValue()));
        }
    }

    private static void print1(LinkedHashMap<Long, Double> hashMap) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        for (Long j : hashMap.keySet()) {
            System.out.println("Item " + j + " --> " + formatter.format(hashMap.get(j).doubleValue()));
        }
    }
}
