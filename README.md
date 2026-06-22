# DB2 + MyBatis CRUD Sample

Spring MVC 구조로 DB2 테이블을 CRUD 하는 샘플입니다.

## 구조

- `CustomerController`: HTTP 요청/응답 처리
- `CustomerService`: 트랜잭션과 비즈니스 흐름 처리
- `CustomerMapper`: MyBatis 매퍼 인터페이스
- `resources/mapper/CustomerMapper.xml`: DB2 SQL 매핑
- `resources/db/customer-schema-db2.sql`: DB2 테이블/샘플 데이터 SQL

## DB 설정

환경변수를 쓰면 운영 환경마다 DB 접속 정보를 바꿀 수 있습니다.

```properties
DB2_URL=jdbc:db2://localhost:50000/SAMPLE
DB2_USERNAME=db2inst1
DB2_PASSWORD=password
```

DB2에서 먼저 아래 SQL 파일을 실행하세요.

```text
src/main/resources/db/customer-schema-db2.sql
```

## 실행

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## API

고객 목록:

```bash
curl http://localhost:8080/api/customers
```

고객 단건 조회:

```bash
curl http://localhost:8080/api/customers/1
```

고객 등록:

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Park Jisoo","email":"jisoo@example.com","phoneNumber":"010-5555-6666"}'
```

고객 수정:

```bash
curl -X PUT http://localhost:8080/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Park Jisoo","email":"jisoo.updated@example.com","phoneNumber":"010-7777-8888"}'
```

고객 삭제:

```bash
curl -X DELETE http://localhost:8080/api/customers/1
```

## Advanced MyBatis / DB2 API

Dynamic SQL search using `<where>` and `<if>`:

```bash
curl "http://localhost:8080/api/customers/search?name=kim&email=example.com"
```

DB2 `MERGE INTO` upsert by email:

```bash
curl -X POST http://localhost:8080/api/customers/upsert \
  -H "Content-Type: application/json" \
  -d '{"name":"Kim Minsoo Updated","email":"minsoo@example.com","phoneNumber":"010-9999-0000"}'
```

DB2 `INSERT INTO ... SELECT ... FROM` copy from an existing customer:

```bash
curl -X POST http://localhost:8080/api/customers/1/copy \
  -H "Content-Type: application/json" \
  -d '{"newEmail":"minsoo.copy@example.com"}'
```

MyBatis `<foreach>` batch staging insert:

```bash
curl -X POST "http://localhost:8080/api/customers/staging?status=READY" \
  -H "Content-Type: application/json" \
  -d '{"customers":[{"name":"Han Seojun","email":"seojun@example.com","phoneNumber":"010-1212-3434"},{"name":"Seo Ara","email":"ara@example.com","phoneNumber":"010-5656-7878"}]}'
```

DB2 `INSERT INTO ... SELECT ... FROM CUSTOMER_STAGING` import:

```bash
curl -X POST "http://localhost:8080/api/customers/import/from-staging?status=READY"
```
