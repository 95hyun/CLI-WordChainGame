

# 🎮 CLI 기반 끝말잇기 게임

끝말잇기 게임은 컴퓨터와 사용자가 번갈아가며 끝말잇기를 진행하는 게임입니다.

국립국어원 사전 API를 활용하여 유효한 단어를 검증하고, 일부 두음법칙을 적용해 게임의 재미를 더했습니다.

사용자가 입력한 단어는 재사용 여부를 확인하며, 공정한 게임이 이루어지도록 설계되었습니다.

이 프로젝트는 LG CNS AM Inspire Camp 1기 Java Mini gruop project 내에도 포함되어 있습니다.

https://github.com/lg-cns-am-1-group3/javaLab


</br>


![asd](https://github.com/user-attachments/assets/c97bbfd3-54c4-4ef2-9a9b-79d1fd906bea)


# 🕹️ 게임 규칙

### 단어 입력 조건:

2글자 이상의 단어만 입력 가능합니다.

단어는 국립국어원 API에서 검색 가능한 단어여야 합니다.

### 패배 조건:

사용된 단어를 재사용하면 패배 처리됩니다.

컴퓨터가 유효한 단어를 찾지 못할 경우, 플레이어가 승리합니다.

### 두음법칙 적용:

일부 단어(예: "락" → "낙")는 두음법칙이 적용되어 처리됩니다.

# 🚀 실행 방법

### 프로젝트 클론
```
git clone https://github.com/your-repo/word-chain-game.git
cd word-chain-game
```

### Java 환경 설정

Java 8 이상이 설치되어 있어야 합니다.


### 컴파일 및 실행

```
javac -d out src/com/wordChain/*.java
java -cp out com.wordChain.Minyeong
```
혹은 IDE 상에서 실행

### API키 발급
API 키를 발급받아, WordChainService.java 의 API_KEY 값에 넣어주시면 정상적으로 구동합니다.

https://opendict.korean.go.kr/service/openApiRegister

실행 후, 11을 입력하고 주제를 선택하여 게임을 시작합니다.



# 📋 주요 기능
### 1. 단어 유효성 검증
국립국어원 사전 검색 API를 통해 입력된 단어가 유효한 단어인지 확인합니다.
두음법칙이 적용되어 특정 조건에서 단어가 자연스럽게 처리됩니다.
### 2. 재사용 단어 확인
사용된 단어는 HashSet 자료구조를 활용한 Repository에 저장되며, 중복 입력 시 패배로 처리됩니다.
### 3. 주제 선택
게임 시작 시 아래 주제 중 하나를 선택하여 진행할 수 있습니다:
사물, 동물, 과일, 직업
자유: 플레이어가 원하는 단어로 직접 게임을 시작할 수 있습니다.
### 4. 컴퓨터의 단어 선택
플레이어의 끝 글자로 시작하는 단어를 API를 통해 검색하고, 결과가 없을 경우 랜덤으로 단어를 선택합니다.
