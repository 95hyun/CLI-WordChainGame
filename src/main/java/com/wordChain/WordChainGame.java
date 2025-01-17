package com.wordChain;

import java.util.Scanner;

public class WordChainGame {
    private final Scanner scanner;
    private final WordChainService service;
    private final UsedWordsRepository usedWordsRepository;

    public WordChainGame(Scanner scanner) {
        this.scanner = scanner;
        this.service = new WordChainService();
        this.usedWordsRepository = new UsedWordsRepository();
    }

    public void start() {
        System.out.println("============= 끝말잇기 게임 =============");
        System.out.println("제시어 주제를 선택하면 컴퓨터부터 시작해요");
        System.out.println("'자유'를 선택하면 사용자가 원하는 단어를 입력해서 먼저 공격해요");
        System.out.println("-------------    Info    --------------");
        System.out.println("일부 단어들은 두음법칙을 적용하고 있어요");
        System.out.println("단어는 국립국어원 사전검색을 기준으로 유효성을 판단하고 있어요");
        System.out.println("2글자 이상의 단어를 입력해야하며, 사용했던 단어는 재사용 시 패배해요");
        System.out.println("=======================================");
        System.out.println("주제: [사물, 동물, 과일, 직업, 자유]");

        System.out.print("주제를 입력하세요: ");
        String category = scanner.nextLine().trim();

        if (!service.isValidCategory(category)) {
            System.out.println("[사물, 동물, 과일, 직업, 자유] 중에서 선택해주세요. 게임 종료");
            return;
        }

        String computerWord = service.initializeGame(category, usedWordsRepository);
        if (computerWord != null) {
            System.out.println("=======================================");
            System.out.println("컴퓨터: " + computerWord);
            System.out.println("=======================================");
        } else {
            System.out.println("자유 주제입니다. 사용자가 먼저 단어를 입력하세요.");
        }

        while (true) {
            System.out.print("사용자 차례! 단어를 입력하세요: ");
            String userWord = scanner.nextLine().trim();

            if (usedWordsRepository.isUsed(userWord)) {
                System.out.println("=======================================");
                System.out.println("저런, 이미 사용된 단어에요. 컴퓨터 승리!");
                break;
            }

            if (!service.isValidWord(userWord, computerWord)) {
                System.out.println("=======================================");
                System.out.println("국어사전에 등재되어 있지 않은 단어에요. 컴퓨터 승리!");
                break;
            }

            usedWordsRepository.addWord(userWord);

            // 컴퓨터 응답 처리
            String computerResponse = service.getComputerResponse(userWord, usedWordsRepository);
            if (computerResponse == null) {
                System.out.println("컴퓨터가 단어를 찾지 못했어요. 사용자 승리!");
                break;
            }

            // 컴퓨터 단어 업데이트
            computerWord = computerResponse;
            System.out.println("=======================================");
            System.out.println("컴퓨터: " + computerResponse);
            System.out.println("=======================================");
        }
    }

    public void resetGame() {
        usedWordsRepository.clear();
    }
}
