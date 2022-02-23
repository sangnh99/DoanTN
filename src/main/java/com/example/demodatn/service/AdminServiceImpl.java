package com.example.demodatn.service;

import com.example.demodatn.constant.RoleConstant;
import com.example.demodatn.domain.*;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.StringUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl {

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public TotalInfoDashboardAdminDomain getTotalInfoDashboard(){
        MetadataEntity metadataEntity = metadataRepository.findById(1l).orElse(null);
        if (metadataEntity == null){
            throw new CustomException("Tinh total cho dashboard bi sai", "Tinh total cho dashboard bi sai", HttpStatus.BAD_REQUEST);
        }

        TotalInfoDashboardAdminDomain domain = new TotalInfoDashboardAdminDomain();
        domain.setTotalIncome(metadataEntity.getTotalIncome());
        domain.setTotalFood(metadataEntity.getTotalFood());
        domain.setTotalUser(userAppRepository.findAll().size());
        domain.setTotalStore(storeRepository.findAll().size());

        return domain;
    }

    public Object getDataForIncomeAndNumberOfOrderChartByMonth() {
        LinkedHashMap<TransactionEntity, String> mapTransaction = transactionRepository.findAllAndOrderByCreatedDate()
                                    .stream()
                                    .collect(Collectors.toMap(Function.identity(), t -> StringUtils.convertDateToStringFormatMMyyyy(t.getCreatedDate()), (v1,v2)->v1,LinkedHashMap::new));
        Map<String, Long> incomeByMonthMap = new LinkedHashMap<>();
        Map<String, Integer> totalOrderByMonthMap = new LinkedHashMap<>();
        for(Map.Entry<TransactionEntity, String> entry : mapTransaction.entrySet()){
            if (incomeByMonthMap.containsKey(entry.getValue())){
                incomeByMonthMap.put(entry.getValue(), incomeByMonthMap.get(entry.getValue()) + entry.getKey().getTotal());
            } else {
                incomeByMonthMap.put(entry.getValue(), entry.getKey().getTotal());
            }
            List<TransactionItemEntity> listItem = transactionItemRepository.findAllByTransactionId(entry.getKey().getId());
            if (CollectionUtils.isEmpty(listItem)){
                throw new CustomException("transactionItem bi sai", "transactionItem bi sai", HttpStatus.BAD_REQUEST);
            }
            Integer totalAmount = listItem.stream()
                    .map(t -> t.getAmount())
                    .reduce(0, (t1, t2) -> t1 + t2);
            if (totalOrderByMonthMap.containsKey(entry.getValue())){
                totalOrderByMonthMap.put(entry.getValue(), totalOrderByMonthMap.get(entry.getValue()) + totalAmount);
            } else {
                totalOrderByMonthMap.put(entry.getValue(), totalAmount);
            }
        }

//        for (Map.Entry<String, Long> entry : incomeByMonthMap.entrySet()){
//            System.out.println(entry.getKey() + " === " + entry.getValue() + "đ");
//        }
        List<IncomeAndTotalOrderByMonthDomain> listResult = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : totalOrderByMonthMap.entrySet()){
            IncomeAndTotalOrderByMonthDomain domain = new IncomeAndTotalOrderByMonthDomain();
            domain.setName("Tháng " + entry.getKey());
            domain.setIncome(incomeByMonthMap.get(entry.getKey()));
            domain.setOrder(totalOrderByMonthMap.get(entry.getKey()));
            listResult.add(domain);
        }

        return listResult;
    }


    public Object getIncomeByStoreChart() {
        LinkedHashMap<TransactionEntity, Long> mapTransaction = transactionRepository.findAllAndOrderByCreatedDate()
                .stream()
                .collect(Collectors.toMap(Function.identity(), t -> t.getStoreId(), (v1,v2)->v1,LinkedHashMap::new));
        Map<Long, Long> incomeByStoreMap = new LinkedHashMap<>();// storeid-income
        for (Map.Entry<TransactionEntity, Long> entry : mapTransaction.entrySet()){
            if (incomeByStoreMap.containsKey(entry.getValue())){
                incomeByStoreMap.put(entry.getValue(), incomeByStoreMap.get(entry.getValue()) + entry.getKey().getTotal());
            } else {
                incomeByStoreMap.put(entry.getValue(), entry.getKey().getTotal());
            }
        }
        incomeByStoreMap = incomeByStoreMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue(), (v1,v2)->v1,LinkedHashMap::new));

//        for (Map.Entry<Long, Long> entry : incomeByStoreMap.entrySet()) {
//            System.out.println(entry.getKey() + " === " + entry.getValue() + "đ");
//        }
        Long total = incomeByStoreMap.entrySet()
                .stream()
                .map(t -> t.getValue())
                .reduce(0l, (t1, t2) -> t1 + t2);
        List<IncomeByStoreDomain> listResult = new ArrayList<>();
        int count = 1;
        Long totalFourStore = 0l;
        for (Map.Entry<Long, Long> entry : incomeByStoreMap.entrySet()){
            if (count == 6){
                break;
            }
            IncomeByStoreDomain domain = new IncomeByStoreDomain();
            StoreEntity storeEntity = storeRepository.findById(entry.getKey()).orElse(null);
            if (storeEntity == null){
                throw new CustomException("store ko ton tai", "store ko ton tai", HttpStatus.BAD_REQUEST);
            }
            domain.setName(storeEntity.getName());
            domain.setValue(entry.getValue());
            listResult.add(domain);
            totalFourStore += entry.getValue();
            count ++;
        }

        IncomeByStoreDomain domain = new IncomeByStoreDomain();
        domain.setName("Khác");
        domain.setValue(total - totalFourStore);
        listResult.add(domain);
        return listResult;
    }

    public Object getUserDataChart() {
        LinkedHashMap<UserAppEntity, String> mapUserApp = userAppRepository.getListUserAppByCreateDate()
                .stream()
                .collect(Collectors.toMap(Function.identity(), t -> StringUtils.convertDateToStringFormatMMyyyy(t.getCreatedDate()), (v1,v2)->v1,LinkedHashMap::new));
        Map<String, Integer> newUserByMonthMap = new LinkedHashMap<>();
        for(Map.Entry<UserAppEntity, String> entry : mapUserApp.entrySet()){
            if (newUserByMonthMap.containsKey(entry.getValue())){
                newUserByMonthMap.put(entry.getValue(), newUserByMonthMap.get(entry.getValue()) + 1);
            } else {
                newUserByMonthMap.put(entry.getValue(), 1);
            }
        }

//        for (Map.Entry<String, Long> entry : incomeByMonthMap.entrySet()){
//            System.out.println(entry.getKey() + " === " + entry.getValue() + "đ");
//        }

        List<NewUserChartDomain> listResult = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : newUserByMonthMap.entrySet()){
            NewUserChartDomain domain = new NewUserChartDomain();
            domain.setName(entry.getKey());
            domain.setValue(entry.getValue());
            listResult.add(domain);
        }
        return listResult;
    }

    public void addNewShipperAdmin(AddNewShipperDomain domain) {
        UserAppEntity userAppEntity = new UserAppEntity();

        UserAppEntity userCheckEmail = userAppRepository.findByEmail(domain.getEmail());
        UserAppEntity userCheckUsername = userAppRepository.findByUsername(domain.getUsername()).orElse(null);

        if (userCheckEmail != null){
            throw new CustomException("Email đã tồn tại, vui lòng nhập email khác !", "Email đã tồn tại, vui lòng nhập email khác !", HttpStatus.BAD_REQUEST);
        }

        if (userCheckUsername != null){
            throw new CustomException("Tên đăng nhập đã tồn tại, vui lòng nhập tên đăng nhập khác !", "Tên đăng nhập đã tồn tại, vui lòng nhập tên đăng nhập khác !", HttpStatus.BAD_REQUEST);
        }


        userAppEntity.setUsername(domain.getUsername());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userAppEntity.setPassword(bCryptPasswordEncoder.encode(domain.getPassword()));
        userAppEntity.setFullName(domain.getFullName());
        userAppEntity.setBirthYear(StringUtils.convertStringToIntegerOrNull(domain.getBirthYear()));
        userAppEntity.setGender(domain.getGender());
        userAppEntity.setEmail(domain.getEmail());
        userAppEntity.setPhone(domain.getPhone());
        userAppEntity.setCmnd(domain.getCmnd());
        userAppEntity.setAddress(domain.getAddress());
        userAppEntity.setAvatar(domain.getAvatar());
        userAppEntity.setIsBusyShipper(0);
        userAppEntity.setIsLocked(0);

        userAppEntity = userAppRepository.save(userAppEntity);

        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setRoleId(RoleConstant.ROLE_SHIPPER.getNumber());
        userRoleEntity.setUserId(userAppEntity.getId());

        userRoleRepository.save(userRoleEntity);
    }

    public void editShipperAdmin(EditShipperRequestDomain domain) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(domain.getId());
        if (shipperId == null){
            throw new CustomException("Shipper ID bi sai", "Shipper ID bi sai", HttpStatus.BAD_REQUEST);
        }
        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);

        if (shipperEntity == null){
            throw new CustomException("Shipper ID bi sai", "Shipper ID bi sai", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity userCheckEmail = userAppRepository.findByEmail(domain.getEmail());

        if (userCheckEmail != null && !userCheckEmail.getId().equals(shipperId)){
            throw new CustomException("Email đã tồn tại, vui lòng nhập email khác !", "Email đã tồn tại, vui lòng nhập email khác !", HttpStatus.BAD_REQUEST);
        }

        shipperEntity.setFullName(domain.getFullName());
        shipperEntity.setBirthYear(StringUtils.convertStringToIntegerOrNull(domain.getBirthYear()));
        shipperEntity.setGender(domain.getGender());
        shipperEntity.setEmail(domain.getEmail());
        shipperEntity.setPhone(domain.getPhone());
        shipperEntity.setCmnd(domain.getCmnd());
        shipperEntity.setAddress(domain.getAddress());
        shipperEntity.setAvatar(domain.getAvatar());

        userAppRepository.save(shipperEntity);
    }
}
