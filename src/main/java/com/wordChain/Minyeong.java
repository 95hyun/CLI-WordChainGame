package com.wordChain;

import java.util.Scanner;

public class Minyeong {
    public void start(Scanner scanner) {
        WordChainGame game = new WordChainGame(scanner);

        while (true) {
            game.start();

            System.out.println("=======================================");
            System.out.print("재시작 하시겠습니까? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            while (!input.equals("y") && !input.equals("n")) {
                System.out.println("=======================================");
                System.out.print("y 또는 n을 입력해주세요: ");
                input = scanner.nextLine().trim().toLowerCase();
            }

            if (input.equals("n")) {
                System.out.println("=======================================");
                System.out.println("게임을 종료합니다");
                break;
            }
            game.resetGame();
        }

        scanner.close();
    }
}
