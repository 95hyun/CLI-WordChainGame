//package com.wordChain;
//
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public class Main {
//    private static final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
//    private static final Map<String, List<String>> predefinedWords = new HashMap<>();
//    private static final String API_URL = "https://opendict.korean.go.kr/api/search";
//    private static final String API_KEY = "8EEF220FACB9F1BC8DE50032A752AE8E";
//
//    // 사전 정의된 주제관련 단어 목록
//    static {
//        predefinedWords.put("과일", Arrays.asList("사과", "수박", "바나나", "포도", "딸기", "자두"));
//        predefinedWords.put("동물", Arrays.asList("고양이", "강아지", "토끼", "코끼리", "사자"));
//        predefinedWords.put("사물", Arrays.asList("책상", "의자", "스피커", "볼펜", "노트"));
//        predefinedWords.put("직업", Arrays.asList("의사", "선생님", "프로그래머", "디자이너", "경찰관", "개발자"));
//        predefinedWords.put("자유", Arrays.asList());
//    }
//
//    public static void main(String[] args) {
//        System.out.println("============= 끝말잇기 게임 =============");
//        System.out.println("제시어 주제를 선택하면 컴퓨터부터 시작해요");
//        System.out.println("'자유'를 선택하면 사용자가 원하는 단어를 입력해서 먼저 공격해요");
//        System.out.println("-------------    Info    --------------");
//        System.out.println("일부 단어들은 두음법칙을 적용하고 있어요");
//        System.out.println("단어는 국립국어원 사전검색을 기준으로 유효성을 판단하고 있어요");
//        System.out.println("2글자 이상의 단어를 입력해야하며, 사용했던 단어는 재사용 시 패배해요");
//        System.out.println("=======================================");
//        System.out.println("주제: [사물, 동물, 과일, 직업, 자유]");
//
//        System.out.print("주제를 입력하세요: ");
//        String category = scanner.nextLine().trim();
//
//        // 주제가 올바르지 않은 경우 종료
//        if (!predefinedWords.containsKey(category)) {
//            System.out.println("[사물, 동물, 과일, 직업, 자유] 중에서 선택해주세요. 게임 종료");
//            return;
//        }
//
//        // 단어 리스트와 사용된 단어 집합 초기화
//        List<String> words = new ArrayList<>(predefinedWords.get(category));
//        Set<String> usedWords = new HashSet<>();
//        boolean isFreeTopic = category.equals("자유");
//
//        String computerWord = null;
//        // 컴퓨터가 선공할 경우 초기 단어 선택
//        if (!isFreeTopic) {
//            computerWord = getRandomWord(words);
//            System.out.println("=======================================");
//            System.out.println("컴퓨터: " + computerWord);
//            System.out.println("=======================================");
//            usedWords.add(computerWord);
//        } else {
//            System.out.println("=======================================");
//            System.out.println("자유 주제! 사용자가 먼저 원하는 단어로 시작해요");
//            System.out.println("=======================================");
//        }
//
//        while (true) {
//            // 사용자 단어 입력
//            System.out.print("사용자 차례! 단어를 입력하세요: ");
//            String userWord = scanner.nextLine().trim();
//
//            // 이미 사용된 단어 검사
//            if (usedWords.contains(userWord)) {
//                System.out.println("=======================================");
//                System.out.println("저런, 이미 사용된 단어에요. 컴퓨터 승리!");
//                break;
//            }
//
//            // 단어 유효성 검사
//            if (!isValidWord(userWord, computerWord)) {
//                System.out.println("=======================================");
//                System.out.println("국어사전에 등재되어 있지 않은 단어에요. 컴퓨터 승리!");
//                break;
//            }
//
//            // 단어 사용 기록 및 다음 단어 시작 문자 설정
//            words.remove(userWord);
//            usedWords.add(userWord);
//            String lastChar = applyInitialSoundChange(getLastCharacter(userWord));
//            System.out.println("=======================================");
//            System.out.println("사용자 입력: " + userWord);
//
//            // 컴퓨터 단어 선택
//            System.out.println("컴퓨터: 단어를 생각 중이에요...");
//            System.out.println("=======================================");
//            computerWord = findWordFromApi(lastChar);
//
//            if (computerWord == null || usedWords.contains(computerWord)) {
//                System.out.println("컴퓨터가 단어를 찾지 못했어요. 사용자 승리!");
//                break;
//            }
//            System.out.println("=======================================");
//            System.out.println("컴퓨터: " + computerWord);
//            System.out.println("=======================================");
//            usedWords.add(computerWord);
//        }
//
//        System.out.println("=======================================");
//        System.out.println("=============== 게임 종료 ===============");
//    }
//
//    // 랜덤 단어 선택
//    private static String getRandomWord(List<String> words) {
//        Random random = new Random();
//        return words.get(random.nextInt(words.size()));
//    }
//
//    // 단어 유효성 검사
//    private static boolean isValidWord(String userWord, String computerWord) {
//        if (computerWord == null) return true;
//
//        String lastChar = applyInitialSoundChange(getLastCharacter(computerWord));
//        String firstChar = userWord.substring(0, 1);
//
//        if (!firstChar.equals(lastChar)) {
//            System.out.println("[DEBUG] 단어의 첫 글자가 이전 단어의 끝 글자와 일치하지 않습니다.");
//            return false;
//        }
//
//        boolean isInDictionary = checkWordInDictionary(userWord);
//        if (!isInDictionary) {
//            System.out.println("[DEBUG] 단어가 사전에 존재하지 않습니다: " + userWord);
//            return false;
//        }
//
//        return true;
//    }
//
//    // 사전에서 단어 존재 여부 확인
//    private static boolean checkWordInDictionary(String word) {
//        try {
//            URL url = new URL(API_URL + "?key=" + API_KEY + "&target_type=search&req_type=json&part=word&q=" + word + "&sort=dict&start=1&num=100");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == 200) {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//
//                    JSONObject json = new JSONObject(response.toString());
//                    if (json.has("channel") && json.getJSONObject("channel").has("item")) {
//                        JSONArray items = json.getJSONObject("channel").getJSONArray("item");
//                        for (int i = 0; i < items.length(); i++) {
//                            if (items.getJSONObject(i).getString("word").replaceAll("-", "").equals(word)) {
//                                return true;
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    System.out.println("[DEBUG] Reader 처리 중 예외 발생: " + e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("[DEBUG] API 호출 중 예외 발생: " + e.getMessage());
//        }
//        return false;
//    }
//
//    // 끝말을 시작 문자로 단어 찾기
//    private static String findWordFromApi(String lastChar) {
//        try {
//            String word = null;
//            List<String> validWords = new ArrayList<>();
//
//            for (int start = 1; start <= 1000; start += 100) {
//                URL url = new URL(API_URL + "?key=" + API_KEY + "&req_type=json&q=" + lastChar + "&start=" + start + "&sort=dict&advanced=y&method=start&type1=word");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == 200) {
//                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//
//                        System.out.println("[DEBUG] API 응답: " + response);
//                        List<String> parsedWords = parseWordSuggestionResponse(response.toString(), lastChar);
//                        validWords.addAll(parsedWords);
//
//                        JSONObject json = new JSONObject(response.toString());
//                        int num = json.getJSONObject("channel").getInt("num");
//                        if (num == 0) {
//                            break; // Stop searching when num is 0
//                        }
//                    } catch (Exception e) {
//                        System.out.println("[DEBUG] Reader 처리 중 예외 발생: " + e.getMessage());
//                    }
//                } else {
//                    System.out.println("[DEBUG] API 호출 실패: 응답 코드 " + responseCode);
//                }
//            }
//
//            if (!validWords.isEmpty()) {
//                Random random = new Random();
//                word = validWords.get(random.nextInt(validWords.size()));
//            }
//
//            return word;
//        } catch (Exception e) {
//            System.out.println("[DEBUG] API 호출 중 예외 발생: " + e.getMessage());
//        }
//        return null;
//    }
//
//    // API 응답에서 단어 목록 추출
//    private static List<String> parseWordSuggestionResponse(String response, String lastChar) {
//        List<String> validWords = new ArrayList<>();
//        JSONObject json = new JSONObject(response);
//        if (json.has("channel") && json.getJSONObject("channel").has("item")) {
//            JSONArray items = json.getJSONObject("channel").getJSONArray("item");
//            for (int i = 0; i < items.length(); i++) {
//                JSONObject item = items.getJSONObject(i);
//                String word = item.getString("word").replaceAll("-", "");
//                if (word.startsWith(lastChar) && word.length() >= 2 && word.matches("^[가-힣]+$") && item.getJSONArray("sense").getJSONObject(0).getString("pos").equals("명사")) {
//                    validWords.add(word);
//                }
//            }
//        }
//        return validWords;
//    }
//
//    // 단어의 마지막 글자 반환
//    private static String getLastCharacter(String word) {
//        return word.substring(word.length() - 1);
//    }
//
//    // 두음법칙 적용
//    private static String applyInitialSoundChange(String character) {
//        if (character == null || character.isEmpty()) return character;
//
//        switch (character) {
//            case "락": return "낙";
//            case "랄": return "날";
//            case "람": return "남";
//            case "랍": return "납";
//            case "랑": return "낭";
//            case "래": return "내";
//            case "랭": return "냉";
//            case "로": return "노";
//            case "록": return "녹";
//            case "론": return "논";
//            case "롤": return "놀";
//            case "롬": return "놈";
//            case "롱": return "농";
//            case "뢰": return "뇌";
//            case "료": return "요";
//            case "륙": return "육";
//            case "률": return "율";
//            case "륜": return "윤";
//            case "륭": return "융";
//            case "라": return "나";
//            case "니": return "이";
//            case "님": return "임";
//            case "륨": return "윰";
//            case "늄": return "윰";
//            default: return character;
//        }
//    }
//}
//
//
//
//
//
