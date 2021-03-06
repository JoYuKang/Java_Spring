package com.deepening.spring_core.service;

import com.deepening.spring_core.Exception.ApiRequestException;
import com.deepening.spring_core.model.Folder;
import com.deepening.spring_core.model.Product;
import com.deepening.spring_core.model.User;
import com.deepening.spring_core.repository.FolderRepository;
import com.deepening.spring_core.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {
    // 멤버 변수 선언
    private final FolderRepository folderRepository;
    private final ProductRepository productRepository;

    // 생성자: ProductFolderService() 가 생성될 때 호출됨
    @Autowired
    public FolderService(FolderRepository folderRepository, ProductRepository productRepository) {
        // 멤버 변수 생성
        this.folderRepository = folderRepository;
        this.productRepository = productRepository;
    }

    // 회원 ID 로 등록된 모든 폴더 조회
    public List<Folder> getFolders(User user) {
        return folderRepository.findAllByUser(user);
    }
    @Transactional // (readOnly = false)
    public List<Folder> createFolders(List<String> folderNameList, User user) {

        List<Folder> folderList = new ArrayList<>();

        for (String folderName : folderNameList) {
            // 1) DB 에 폴더명이 folderName 인 폴더가 존재하는지?
            Folder folderInDB = folderRepository.findByName(folderName);
            if (folderInDB != null) {
                // DB 에 중복 폴더명 존재한다면 Exception 발생시킴
                throw new ApiRequestException("중복된 폴더명 ('" + folderName + "') 을 삭제하고 재시도해 주세요!");
            }

            // 2) 폴더를 DB 에 저장
            Folder folder = new Folder(folderName, user);
            folder = folderRepository.save(folder);

            // 3) folderList 에 folder Entity 객체를 추가
            folderList.add(folder);
        }

        return folderList;
    }
/*  중복제거 방법 1
    문제점 : Reposiroty를 delete 할 때 문제가 발생 할 수 있다.
    public List<Folder> createFolders(List<String> folderNameList, User user) {
        List<Folder> folderList = new ArrayList<>();

        for (String folderName : folderNameList) {
            // 1) DB 에 폴더명이 folderName 인 폴더가 존재하는지?
            Folder folderInDB = folderRepository.findByName(folderName);
            if (folderInDB != null) {
                // 그동안 저장된 폴더들을 모두 삭제!
                for (Folder folder : folderList) {
                    folderRepository.delete(folder);
                }

                // DB 에 중복 폴더명 존재한다면 Exception 발생시킴
                throw new IllegalArgumentException("중복된 폴더명 (" + folderName +") 을 삭제하고 재시도해 주세요!");
            }

            // 2) 폴더를 DB 에 저장
            Folder folder = new Folder(folderName, user);
            folder = folderRepository.save(folder);

            // 3) folderList 에 folder Entity 객체를 추가
            folderList.add(folder);
        }

        return folderList;
    }

 */

//    public List<Folder> createFolders(List<String> folderNameList, User user) {
//        //1.입력으로 들어온 폴더 이름을 기준으로 회원이 이미 생성한 폴더를 조회
//        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNameList);
//
//        List<Folder> folderList = new ArrayList<>();
//
//        for (String folderName : folderNameList) {
//            // 2. 이미 새성한 폴더가 아닌 경우 폴더 생성
//            if(!isExistFolderName(folderName,existFolderList)) {
//                Folder folder = new Folder(folderName, user);
//                folderList.add(folder);
//            }
//        }
//        folderList = folderRepository.saveAll(folderList);
//        return folderList;
//    }

    public boolean isExistFolderName(String foldername, List<Folder> isExistFolderName){
        //기존 폴더 리스트에 folder name이 존재하는지 확인
        for(Folder existFolder : isExistFolderName){
            if(existFolder.getName().equals(foldername)){
                return true;
            }
        }
        return false;
    }


    // 회원 ID 가 소유한 폴더에 저장되어 있는 상품들 조회
    public Page<Product> getProductsOnFolder(User user, int page, int size, String sortBy, boolean isAsc, Long folderId) {
        // 페이지를 위한 부분
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAllByUserIdAndFolderList_Id(user.getId(), folderId, pageable);
    }
}