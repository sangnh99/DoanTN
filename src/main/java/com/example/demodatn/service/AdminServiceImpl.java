package com.example.demodatn.service;

import com.example.demodatn.domain.IncomeAndTotalOrderByMonthDomain;
import com.example.demodatn.domain.IncomeByStoreDomain;
import com.example.demodatn.domain.NewUserChartDomain;
import com.example.demodatn.domain.TotalInfoDashboardAdminDomain;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.StringUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
}
