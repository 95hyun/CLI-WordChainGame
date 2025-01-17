package com.wordChain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class WordChainService {
    private static final Map<String, List<String>> predefinedWords = new HashMap<>();
    private static final String API_URL = "https://opendict.korean.go.kr/api/search";
    private static final String API_KEY = "우리말샘 API키를 직접 발급받아 이 곳에 입력하세요";
    private String lastCharacter;

    static {
        predefinedWords.put("과일", Arrays.asList("사과", "수박", "바나나", "포도", "딸기", "자두"));
        predefinedWords.put("동물", Arrays.asList("고양이", "강아지", "토끼", "코끼리", "사자"));
        predefinedWords.put("사물", Arrays.asList("책상", "의자", "스피커", "볼펜", "노트"));
        predefinedWords.put("직업", Arrays.asList("의사", "선생님", "프로그래머", "디자이너", "경찰관"));
        predefinedWords.put("자유", Collections.emptyList());
    }

    public boolean isValidCategory(String category) {
        return predefinedWords.containsKey(category);
    }

    public String initializeGame(String category, UsedWordsRepository usedWordsRepository) {
        if (!category.equals("자유")) {
            String word = getRandomWord(predefinedWords.get(category));
            usedWordsRepository.addWord(word);
            lastCharacter = getLastCharacter(word);
            return word;
        }
        return null;
    }

    public boolean isValidWord(String userWord, String computerWord) {
        if (computerWord == null) return true;

        // 컴퓨터 단어의 끝 글자 추출 및 두음법칙 적용
        String lastChar = applyInitialSoundChange(getLastCharacter(computerWord));
        String firstChar = userWord.substring(0, 1);

        // 두 글자 비교
        if (!firstChar.equals(lastChar)) {
            System.out.printf("[DEBUG] 단어 '%s'의 첫 글자 '%s'가 이전 단어 '%s'의 끝 글자 '%s'와 일치하지 않습니다.%n",
                    userWord, firstChar, computerWord, lastChar);
            return false;
        }

        // 단어 유효성 확인
        return checkWordInDictionary(userWord);
    }

    public String getComputerResponse(String userWord, UsedWordsRepository usedWordsRepository) {
        // 사용자 단어의 끝 글자 추출
        lastCharacter = applyInitialSoundChange(getLastCharacter(userWord));

        System.out.println("=======================================");
        System.out.println("컴퓨터가 단어를 생각하고 있어요...");
        List<String> validWords = findWordsFromApi(lastCharacter);

        // 유효한 단어 필터링
        for (String word : validWords) {
            if (!usedWordsRepository.isUsed(word) && word.length() >= 2) {
                usedWordsRepository.addWord(word);
                return word; // 컴퓨터 단어로 반환
            }
        }

        return null; // 유효한 단어가 없을 경우 null 반환
    }

    private List<String> findWordsFromApi(String lastChar) {
        List<String> validWords = new ArrayList<>();
        try {
            for (int start = 1; start <= 1000; start += 100) {
                // API 요청 URL 구성
                String queryUrl = API_URL + "?key=" + API_KEY +
                        "&req_type=json&q=" + lastChar +
                        "&start=" + start +
                        "&sort=dict&advanced=y&method=start&type1=word";

                HttpURLConnection connection = (HttpURLConnection) new URL(queryUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        System.out.println("[DEBUG] API 응답: " + response);
                        JSONObject json = new JSONObject(response.toString());

                        // 응답 데이터에서 단어 추출
                        if (json.has("channel") && json.getJSONObject("channel").has("item")) {
                            JSONArray items = json.getJSONObject("channel").getJSONArray("item");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                String word = item.getString("word").replaceAll("-", "");
                                if (word.startsWith(lastChar) &&
                                        word.length() >= 2 &&
                                        word.matches("^[가-힣]+$") &&
                                        item.getJSONArray("sense").getJSONObject(0).getString("pos").equals("명사")) {
                                    validWords.add(word);
                                }
                            }
                        }

                        // 응답에 단어가 없을 경우 반복 종료
                        if (json.has("channel") && json.getJSONObject("channel").getInt("num") == 0) {
                            break;
                        }
                    }
                } else {
                    System.out.println("[DEBUG] API 호출 실패: 응답 코드 " + responseCode);
                }
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] API 호출 중 오류 발생: " + e.getMessage());
        }

        return validWords; // 유효한 단어 리스트 반환
    }


    private boolean checkWordInDictionary(String word) {
        try {
            URL url = new URL(API_URL + "?key=" + API_KEY + "&req_type=json&q=" + word);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject json = new JSONObject(response.toString());
                    if (json.has("channel") && json.getJSONObject("channel").has("item")) {
                        JSONArray items = json.getJSONObject("channel").getJSONArray("item");
                        for (int i = 0; i < items.length(); i++) {
                            if (items.getJSONObject(i).getString("word").replaceAll("-", "").equals(word)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] API 호출 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    private String getRandomWord(List<String> words) {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    private String getLastCharacter(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(word.length() - 1);
    }

    // 두음법칙 적용
    private static String applyInitialSoundChange(String character) {
        if (character == null || character.isEmpty()) return character;

        switch (character) {
            case "락": return "낙";
            case "랄": return "날";
            case "람": return "남";
            case "랍": return "납";
            case "랑": return "낭";
            case "래": return "내";
            case "랭": return "냉";
            case "로": return "노";
            case "록": return "녹";
            case "론": return "논";
            case "롤": return "놀";
            case "롬": return "놈";
            case "롱": return "농";
            case "뢰": return "뇌";
            case "료": return "요";
            case "륙": return "육";
            case "률": return "율";
            case "륜": return "윤";
            case "륭": return "융";
            case "니": return "이";
            case "님": return "임";
            case "륨": return "윰";
            case "늄": return "윰";
            default: return character; // 변환이 필요 없는 문자는 그대로 반환
        }
    }

}