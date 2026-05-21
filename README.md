# AURA Unified

Du an nay gop cac goi AURA thanh mot workspace web co backend Spring Boot va frontend React.

## Cau truc

- `src/main/java`: backend Spring Boot dung MySQL/XAMPP.
- `frontend`: giao dien React/Vite thong nhat, tach ro Admin Console va User Workspace.
- `database/init.sql`: tao database `aura_unified` neu can tao thu cong trong phpMyAdmin.

## Chay voi XAMPP

1. Mo XAMPP va bat MySQL.
2. Vao phpMyAdmin, import `database/init.sql` hoac tao database ten `aura_unified`.
3. Chay backend:

```powershell
mvn spring-boot:run
```

4. Cach nhanh nhat: mo truc tiep giao dien tinh duoc Spring Boot phuc vu:

```text
http://localhost:8080
```

Neu may da cai Node.js/npm, ban cung co the chay frontend Vite rieng:

```powershell
cd frontend
npm install
npm run dev
```

Sau do truy cap `http://localhost:5173`.

## Tai khoan mau

Backend se tu tao du lieu mau khi chay lan dau:

- Admin: `admin@aura.local` / `123456`
- Doctor: `doctor@aura.local` / `123456`
- User: `user@aura.local` / `123456`

## Cau hinh database

Mac dinh backend ket noi:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/aura_unified?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh
spring.datasource.username=root
spring.datasource.password=
```

Neu XAMPP cua ban co mat khau MySQL, sua `src/main/resources/application.properties`.
