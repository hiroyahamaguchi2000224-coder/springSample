# Spring Sample Application

Spring Boot 3.5.8 / Java 21 / PostgreSQL を使用したWebアプリケーションサンプル。

**Last Updated**: 2025年12月4日

---

## 📝 主要な設計変更履歴

### 2025年12月4日
- **メニュー画面の実装方式変更**:
  - メニューテーブル(TM_MENU)を使用した動的生成を廃止
  - ボタンベタ書き + PRGパターンに変更
  - VZ0102Controllerに各画面遷移用のPOSTメソッドを追加

- **Controller定数の設計変更**:
  - VIEW定数をprivateに変更(PRGパターンのため外部参照不要)
  - VIEW定数を `"pages" + PATH + "/index"` の動的定義に統一(DRY原則)

- **VZ9901ドライバ画面の修正**:
  - 全ボタンに `formmethod="get"` を明示的に追加
  - POST不可の画面への誤POSTエラーを解消

- **Formバインディングとデータマッピングの簡略化**:
  - `@ModelAttribute(FORM)` アノテーションを省略（Springが自動検出）
  - ModelMapperを導入してDTO/Entity間のマッピングを明示化
  - `th:object` をHTMLの `<form>` タグに配置

---

## 📋 目次

- [主要な設計変更履歴](#主要な設計変更履歴)
- [アーキテクチャ](#アーキテクチャ)
- [ログ方針](#ログ方針)
- [エラーコード体系](#エラーコード体系)
- [トークンフロー（二重送信防止)](#トークンフロー二重送信防止)
- [セキュリティ設定](#セキュリティ設定)
- [機能ID・画面ID命名規則](#機能id画面id命名規則)
- [開発環境](#開発環境)
- [ビルド・実行](#ビルド実行)

---

## 🏗 アーキテクチャ

### レイヤー構成

```
┌─────────────────────────────────────┐
│          View Layer                 │  Thymeleaf テンプレート
│  (templates/pages, fragments)       │  + 共通JS (common.js)
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│       Controller Layer              │  @Controller
│  (controller パッケージ)             │  - リクエスト受付
│  - VZ: 共通機能                      │  - Form バインド
│  - VA: 機能群A                       │  - View 選択
│  - VB: 機能群B                       │  - 個別 ServiceException 捕捉
│  ※メニュー画面(VZ0102)は           │  - PRGパターンで各画面に遷移
│    メニューテーブル不使用、          │    (POST→リダイレクト→GET)
│    ボタンベタ書きで実装              │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│        Service Layer                │  @Service
│  (service パッケージ)                │  - 業務ロジック
│  - LoginService (認証)               │  - トランザクション管理
│  - VA0101Service, VB0101Service     │  - Mapper 呼出
│                                     │  - ErrorCodes 使用
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│       Mapper Layer (MyBatis)        │  @Mapper
│  (mapper パッケージ + XML)           │  - SQL 実行
│  - AccountMapper                    │  - Entity マッピング
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         Database Layer              │  PostgreSQL
│  (init_schema.sql, init_data.sql)   │  - TM_USER テーブル
│                                     │  - TM_COMPANY テーブル
└─────────────────────────────────────┘
```

### メニュー画面の実装方針

```java
// VZ0102Controller: 各画面への遷移メソッド
@PostMapping("/toVa0101")
@ActivateToken(type = ActivateToken.TokenType.VALIDATE)
public String toVa0101() {
    return VA0101Controller.REDIRECT;  // "redirect:/va0101"
}
```

```html
<!-- VZ0102 index.html: ボタンベタ書き -->
<button type="submit" class="btn btn-primary" th:formaction="@{/vz0102/toVa0101}">
  <div class="fw-bold">VA0101 - 商品検索</div>
  <small class="text-white-50">商品検索・カート追加</small>
</button>
```

### Controller定数の設計方針

全Controllerで以下の定数を定義し、一貫性を保ちます:

```java
public class VA0101Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/va0101";
    
    /** 表示するテンプレート名(private: 外部参照不要) */
    private static final String VIEW = "pages" + PATH + "/index";
    
    /** 自画面へのリダイレクト定数(外部から参照可能) */
    public static final String REDIRECT = "redirect:" + PATH;
    
    /** フォーム名定数(ModelAttributeとHTMLで統一) */
    public static final String FORM = "va0101Form";
}
```

**VIEW定数がprivateの理由**:
- **PRGパターン**: 他のControllerから直接VIEWを参照する必要がない
- **リダイレクトで遷移**: 遷移先はREDIRECT定数を使用
- **カプセル化**: 各Controllerの内部実装の詳細を隠蔽

**VIEW定数の定義方式**:
- **動的定義**: `"pages" + PATH + "/index"` でPATHから自動生成
- **DRY原則**: パスの重複記述を排除
- **保守性向上**: PATHを変更すればVIEWも自動的に更新

### 横断的関心事 (AOP / Filter)

- **InvocationLoggingAspect**: Controller/Service 全メソッドの呼出/終了ログ出力
- **ActivateTokenInterceptor**: 二重送信防止トークン (CREATE/VALIDATE)
- **GlobalExceptionHandler**: 全例外の共通捕捉 → VZ0103 (共通エラー画面) へリダイレクト
- **Spring Security Filter**: CSRF 保護、認証/認可、セッション管理

---

## 📊 ログ方針

### ログレベル使い分け

| レベル  | 用途                                                   | 例                                       |
|---------|--------------------------------------------------------|------------------------------------------|
| **DEBUG** | メソッド呼出トレース、内部処理フロー、開発時デバッグ | `log.debug("呼出: {}.{}", class, method)` |
| **INFO**  | 業務イベント、正常処理完了                             | `log.info("ログイン成功: user={}", id)`   |
| **WARN**  | 警告（処理継続可能だが注意が必要）                      | `log.warn("トークン検証失敗: uri={}")`    |
| **ERROR** | エラー（処理中断、要対応）                             | `log.error("DB接続失敗", exception)`      |

### 自動ログ出力 (InvocationLoggingAspect)

- **対象**: `controller` / `service` パッケージ配下の全 public メソッド
- **出力内容**:
  - 呼出前: `呼出: {完全修飾クラス名}.{メソッド名} 引数={引数配列}`
  - 終了後: `終了: {完全修飾クラス名}.{メソッド名}` ※正常終了かどうかは不明
- **ログレベル**: `DEBUG`

### 個別ログ推奨箇所

- **ビジネスロジックの重要判断**: 分岐条件や例外スロー前
- **外部システム連携**: API 呼出前後
- **パフォーマンス計測**: 時間のかかる処理の前後

### セキュリティ・個人情報保護

#### ログ出力禁止事項

以下の情報は**絶対にログ出力しない**こと:

- **パスワード**: 平文、ハッシュ化前の値
- **クレジットカード情報**: カード番号、CVV、有効期限
- **個人情報**: マイナンバー、免許証番号、口座番号
- **セッションID**: 完全なセッショントークン
- **APIキー・シークレット**: 認証トークン、秘密鍵

#### マスキングが必要な情報

```java
// ❌NG: ユーザーIDをそのまま出力
log.info("ログイン成功: userId={}", userId);

// ✅OK: ユーザーIDの一部をマスキング
log.info("ログイン成功: userId={}***", userId.substring(0, 3));

// ❌NG: メールアドレスをそのまま出力
log.info("メール送信: to={}", email);

// ✅OK: メールアドレスをマスキング
log.info("メール送信: to={}@***", email.split("@")[0]);
```

#### IPアドレスの記録

```java
// セキュリティ監査用（認証失敗、不正アクセス検知）のみ記録
log.warn("認証失敗: IP={}, User={}", request.getRemoteAddr(), username);
```

**注意**: 通常の業務処理ではIPアドレスを記録しない（個人情報保護法対応）

---

## 🔢 エラーコード体系

### メッセージ管理方式

- **エラーメッセージ**: `messages.properties` で一元管理
- **バリデーションメッセージ**: `ValidationMessages.properties` で Spring 標準に準拠
- **MessageSource**: `MessageConfig` で設定、各 Service や GlobalExceptionHandler で DI して使用
- **エラーコードキー**: 文字列リテラルで直接指定（例: `"E0001"`, `"E_VA0101_001"`）

### メッセージ表示の仕組み

#### 通常メッセージ表示（各画面）

Controller で `Message` ユーティリティクラスを使用してフラッシュスコープにメッセージを追加:

```java
@Autowired
private Message message;

// 通常メッセージ
message.addFlashMessage(redirectAttributes, "I0001");

// 引数付きメッセージ
message.addFlashMessage(redirectAttributes, "I0002", new Object[]{"ユーザー名"});

return "redirect:/vz0102";
```

各画面では `message` フラグメントで自動表示:
```html
<!-- layout/main.html に含まれる -->
<div th:replace="fragments/message :: message"></div>
```

#### エラーメッセージ表示（共通エラー画面 VZ0103）

GlobalExceptionHandler で自動的にフラッシュスコープに設定し、VZ0103 にリダイレクト:
```java
message.addFlashErrorMessage(redirectAttributes, "E0001", uri);
return "redirect:/vz0103";
```

VZ0103 では通常の `message` フラグメント + 追加情報（発生画面名）を表示。

### ログ用メッセージ取得

ログ出力のみで画面表示不要な場合は `MessageSource` を直接使用:
```java
@Autowired
private MessageSource messageSource;

String logMessage = messageSource.getMessage("E_VA0101_001", null, null);
log.error("エラー発生: {}", logMessage);
```

### コード形式

| 接頭辞 | 用途             | 範囲           | 例            |
|--------|------------------|----------------|---------------|
| **E**  | エラー           | E0001～        | E0100, E_VA0101_001 |
| **W**  | 警告             | W0001～        | W0001         |
| **I**  | 情報             | I0001～        | I0001         |

### エラーコード定義

Service でのメッセージ取得例:
```java
String message = messageSource.getMessage("E_VA0101_001", null, null);
throw new ServiceException("E_VA0101_001", message);
```

GlobalExceptionHandler でのメッセージ取得例:
```java
String message = messageSource.getMessage("E0001", null, null);
model.addAttribute("errorMessage", message);
model.addAttribute("errorScreen", uri);
return "pages/vz0103/index";
```

### コード区分

| エラーコード範囲 | 分類 | HTTPステータス | 用途 |
|----------------|------|---------------|------|
| **E0001～E0099** | システム共通エラー | 500 Internal Server Error | DB接続失敗、予期しない例外 |
| **E0100～E0199** | 認証・認可エラー | 401 Unauthorized / 403 Forbidden | 認証失敗、権限不足 |
| **E0200～E0299** | バリデーションエラー | 400 Bad Request | 入力値検証エラー |
| **E0300～E0399** | リソースエラー | 404 Not Found | データ未存在 |
| **E1000～** | 業務ロジックエラー | 400 Bad Request / 422 Unprocessable Entity | 機能ID付き業務エラー |
| **W0001～** | 警告 | 200 OK（処理継続） | 注意喚起が必要な状況 |
| **I0001～** | 情報 | 200 OK | 正常処理完了 |

**HTTPステータスコードとの対応**:
- Webアプリケーションでは主にリダイレクトを使用するため、ステータスコードは302/200が多い
- エラー時はVZ0103（共通エラー画面）へリダイレクトし、エラーコードをフラッシュスコープで渡す
- REST API実装時は、上記のHTTPステータスコードを適切に返却する

### バリデーション

Spring 標準の Bean Validation を使用:
```java
@NotBlank
@Size(min = 1, max = 100)
private String parameter;
```

メッセージは `ValidationMessages.properties` で定義:
```properties
NotBlank.VA0101Form.parameter=パラメータは必須です
Size.VA0101Form.parameter=パラメータは1文字以上100文字以内で入力してください
```

---

## 🔐 トークンフロー（二重送信防止）

### 概要

サーバ側で生成したワンタイムトークンをセッションに保持し、フォーム送信時に検証することで二重送信を防止。  
**検証後は自動的に新しいトークンが再生成される**ため、アノテーション1個で完結。

### トークン生成 (CREATE)

1. **Controller**: GET メソッドに `@ActivateToken(type = ActivateToken.TokenType.CREATE)` を付与
   ```java
   @ActivateToken(type = ActivateToken.TokenType.CREATE)
   @GetMapping
   public String show(Model model) { ... }
   ```
2. **Interceptor**: `ActivateTokenInterceptor.createToken()` が呼ばれる
3. **TokenHelper**: `generateToken()` で Base36 ランダム文字列（165ビット）生成
4. **Session**: `_token` に保存（キー名とパラメータ名を統一）
5. **Request**: 同じ値を `request.setAttribute("_token", token)` で設定
6. **View**: `CompositeRequestDataValueProcessor` が `<form>` に自動挿入
   ```html
   <input type="hidden" name="_token" value="生成されたトークン">
   ```

### トークン検証 (VALIDATE)

1. **Controller**: POST メソッドに `@ActivateToken(type = ActivateToken.TokenType.VALIDATE)` を付与
   ```java
   @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
   @PostMapping("/execute")
   public String execute(Form form, RedirectAttributes redirectAttributes) {
       // 業務処理
       service.execute(form);
       
       // メッセージ設定
       message.addFlashMessage(redirectAttributes, "I0001");
       
       // リダイレクト
       return "redirect:/vz0102";
   }
   ```
2. **Interceptor**: `ActivateTokenInterceptor.validateToken()` が呼ばれる
3. **検証処理**:
   - セッションから `_token` を取得
   - リクエストパラメータ `_token` を取得
   - 両者を比較
4. **検証成功**: **自動的に新しいトークンを生成してセッションに保存**（再生成忘れ防止）
5. **検証失敗**: `InvalidTokenException` をスロー → `GlobalExceptionHandler` で捕捉 → VZ0103 へリダイレクト

### POST処理の実装パターン

**重要**: `@ActivateToken(type=VALIDATE)` だけで検証後に自動的に新トークンが生成されます。

#### パターン1: データ更新処理（登録・更新・削除） - PRGパターン必須

**データを変更する処理は必ずリダイレクトを使用してください。**

```java
// 更新・削除・登録などのデータ更新処理
@ActivateToken(type = ActivateToken.TokenType.VALIDATE)
@PostMapping("/update")
public String update(@Validated UpdateForm form, BindingResult result, 
                     RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
        return VIEW;  // バリデーションエラー時は自画面
    }
    
    // データ更新処理
    service.update(form);
    
    // メッセージ設定
    message.addFlashMessage(redirectAttributes, "I0001", new Object[]{"更新"});
    
    // 必ずリダイレクト（PRGパターン）
    return "redirect:/menu";  // メニューまたは一覧画面へ
}

// リダイレクト先のGET
@ActivateToken(type = ActivateToken.TokenType.CREATE)
@GetMapping
public String index(Model model) {
    // 新しいトークンが生成される
    return VIEW;
}
```

**PRGパターンを使う理由**:
- ブラウザの戻るボタン対策（戻る→進むで再POST防止）
- URLがGETエンドポイントになる（ブックマーク・共有可能）
- セッションタイムアウト後の挙動が安全

#### パターン2: 検索処理 - POST → VIEW直接表示OK

**データを変更しない検索処理は、POST後に直接VIEWを返してもOKです。**

```java
// 検索処理（データ更新なし）
@ActivateToken(type = ActivateToken.TokenType.VALIDATE)
@PostMapping("/search")
public String search(@Validated SearchForm form, BindingResult result, Model model) {
    if (result.hasErrors()) {
        return VIEW;  // バリデーションエラー
    }
    
    // 検索実行（SELECT処理のみ、データ更新なし）
    List<Product> products = service.search(form);
    
    // 検索結果と条件を画面に設定
    model.addAttribute("products", products);
    model.addAttribute("searchForm", form);  // 検索条件を保持
    
    // 直接VIEW返却（検証後に自動でトークン再生成）
    return VIEW;
}
```

**POST→VIEW直接表示が許容される理由**:
- データ更新がない（再実行しても副作用なし）
- 検索条件を画面に保持できる
- GETだとURL長制限、検索条件がURLに露出

**F5キーは禁止していますが、PRGパターンを推奨する理由**:
- ブラウザの戻るボタンは禁止できない
- URLをブックマークや共有する際の問題回避
- セッション管理の安全性向上

### 実装パターンまとめ

#### Webアプリケーション（本アプリケーション）

| 処理種別 | HTTPメソッド | 実装パターン | 理由 |
|---------|-------------|------------|------|
| **登録・更新・削除** | POST | POST → リダイレクト → GET（PRG） | データ更新あり、二重送信防止必須 |
| **検索** | POST | POST → VIEW直接表示 | データ更新なし、検索条件保持 |
| **初期表示** | GET | GET → VIEW | 画面表示のみ |
| **バリデーションエラー** | - | POST → VIEW直接表示 | 入力値保持のため自画面表示 |

#### RESTful APIとの違い

本アプリケーションは**サーバーサイドレンダリング（SSR）のWebアプリケーション**であり、RESTful APIとは異なる設計方針を採用しています:

| 項目 | 本アプリケーション（SSR） | RESTful API |
|-----|------------------------|-------------|
| **検索処理** | POST（検索条件を隠蔽） | GET（クエリパラメータ） |
| **レスポンス** | HTML（Thymeleaf） | JSON/XML |
| **エラー処理** | リダイレクト→エラー画面 | HTTPステータス + エラーJSON |
| **セッション** | 使用（CSRF、トークン） | ステートレス（JWT等） |
| **冪等性** | PRGパターンで担保 | HTTPメソッドで担保 |

**検索にPOSTを使用する理由**:

1. **セキュリティ**: 検索条件がURLに表示されない
   ```
   GET: /search?keyword=機密情報&date=2025-01-01  ❌ URLに露出
   POST: /search  ✅ リクエストボディで送信
   ```

2. **URL長制限**: 複雑な検索条件でもエラーにならない
   ```
   GET: 最大2048文字程度（ブラウザ依存）
   POST: 制限なし（数MB可能）
   ```

3. **ユーザビリティ**: 検索条件を画面に保持できる
   ```java
   // POST後にVIEW返却で検索条件を画面に再表示
   model.addAttribute("searchForm", form);
   return VIEW;
   ```

**REST APIを実装する場合の推奨パターン**:

```java
// REST APIエンドポイント例（別途実装する場合）
@RestController
@RequestMapping("/api/v1")
public class ProductApiController {
    
    // 検索はGET（RESTful原則に従う）
    @GetMapping("/products")
    public ResponseEntity<List<Product>> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category) {
        // ...
    }
    
    // 登録はPOST
    @PostMapping("/products")
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        // ...
    }
}
```

### トークン再生成タイミング

- **各画面 GET**: `@ActivateToken(type=CREATE)` で新規生成
- **POST処理時**: `@ActivateToken(type=VALIDATE)` で検証後に自動再生成
- **ログイン失敗時**: `CustomAuthenticationFailureHandler` が新トークン生成
- **エラー発生時**: `GlobalExceptionHandler` でVZ0103へリダイレクト後、VZ0103のGETで再生成

### CSRF トークンとの併用

- **CSRF**: Spring Security が自動生成（`CsrfRequestDataValueProcessor`）
- **二重送信防止**: カスタム実装（`ActivateTokenInterceptor`）
- **併存**: `CompositeRequestDataValueProcessor` が両方を `<form>` に自動挿入

---

## 🔒 セキュリティ設定

### セキュリティヘッダー

Spring Security により以下のセキュリティヘッダーが自動設定されます:

```java
// SecurityConfig.java で設定
http.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'")
    )
    .frameOptions(frame -> frame.deny())  // X-Frame-Options: DENY
    .xssProtection(xss -> xss.disable())  // X-XSS-Protection（CSP優先）
    .contentTypeOptions(Customizer.withDefaults())  // X-Content-Type-Options: nosniff
);
```

| ヘッダー名 | 設定値 | 目的 |
|-----------|--------|------|
| **Content-Security-Policy** | `default-src 'self'` | XSS攻撃防止、外部リソース制限 |
| **X-Frame-Options** | `DENY` | クリックジャッキング防止 |
| **X-Content-Type-Options** | `nosniff` | MIME スニッフィング防止 |
| **Strict-Transport-Security** | `max-age=31536000` | HTTPS強制（本番環境） |

### HTTPS設定（本番環境必須）

**開発環境**: HTTP（localhost）で動作

**本番環境**: 必ずHTTPSを使用してください。

```yaml
# application.yml（本番環境）
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
  port: 8443

spring:
  security:
    require-ssl: true
```

**HSTS（HTTP Strict Transport Security）**を有効化:

```java
http.headers(headers -> headers
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubDomains(true)
        .maxAgeInSeconds(31536000)
    )
);
```

### パスワードポリシー

#### ハッシュアルゴリズム

- **アルゴリズム**: BCrypt
- **ストレングス**: 12（2^12回の反復）
- **理由**: OWASP推奨、レインボーテーブル攻撃耐性

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // strength=12
}
```

**ストレングス変更時の注意**:
- 値を大きくすると安全性向上、処理時間増加
- 推奨範囲: 10～14
- 本番環境では最低12以上

#### パスワード要件（推奨）

```java
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
         message = "パスワードは8文字以上、大小英字・数字・記号を含む必要があります")
private String password;
```

- **長さ**: 8文字以上
- **文字種**: 大文字・小文字・数字・記号を各1文字以上
- **禁止**: 辞書単語、連続文字（123, abc等）

### セッション管理

```java
// SecurityConfig.java
http.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    .maximumSessions(1)  // 同時ログイン数制限
    .maxSessionsPreventsLogin(false)  // 新しいログインで古いセッション無効化
    .expiredUrl("/vz0101?expired")
);
```

| 設定項目 | 設定値 | 説明 |
|---------|--------|------|
| **セッションタイムアウト** | 30分（デフォルト） | application.ymlで変更可能 |
| **同時ログイン数** | 1 | 同一ユーザーの複数端末ログイン禁止 |
| **セッション固定攻撃対策** | 有効 | ログイン時にセッションID再生成 |

```yaml
# application.yml
server:
  servlet:
    session:
      timeout: 30m  # セッションタイムアウト
      cookie:
        http-only: true  # JavaScript アクセス禁止
        secure: true     # HTTPS のみ（本番環境）
        same-site: strict  # CSRF 対策
```

### 認証・認可

#### 認証失敗時の対応

```java
// CustomAuthenticationFailureHandler.java
@Override
public void onAuthenticationFailure(...) {
    // セキュリティ監査ログ（詳細情報）
    log.warn("認証失敗: IP={}, User={}, Reason={}", 
        request.getRemoteAddr(),
        request.getParameter("username"),
        exception.getClass().getSimpleName());
    
    // ユーザーには統一メッセージ（情報漏洩防止）
    // 「ユーザー名またはパスワードが間違っています」
}
```

**セキュリティ原則**:
- ユーザー存在チェックとパスワード不一致を区別しない
- 攻撃者にアカウント存在情報を与えない

#### ブルートフォース攻撃対策

**推奨実装**（現状未実装）:

```java
// ログイン試行回数制限（5回失敗でアカウントロック）
// IP単位のレート制限（1分間に10回まで）
// CAPTCHA導入（3回失敗後）
```

### 監査ログ

以下のイベントは監査ログに記録:

- **認証**: ログイン成功/失敗、ログアウト
- **認可**: アクセス拒否、権限エラー
- **データ変更**: 登録・更新・削除操作
- **セキュリティイベント**: トークン検証失敗、セッションタイムアウト

```java
log.info("認証成功: userId={}, IP={}", userId, remoteAddr);  // INFO
log.warn("認証失敗: IP={}, attemptUser={}", remoteAddr, username);  // WARN
log.error("不正アクセス検知: IP={}, URL={}", remoteAddr, requestUri);  // ERROR
```

### 実装済みセキュリティ機能

- ✅ **CSRF 保護**: Spring Security 自動生成トークン
- ✅ **二重送信防止**: カスタムトークン検証
- ✅ **パスワードハッシュ**: BCrypt（strength=12）
- ✅ **セッション固定攻撃対策**: ログイン時ID再生成
- ✅ **同時ログイン制限**: 最大1セッション
- ✅ **認証失敗時の統一メッセージ**: 情報漏洩防止
- ✅ **XSS対策**: Thymeleaf自動エスケープ
- ✅ **SQLインジェクション対策**: MyBatis PreparedStatement
- ✅ **クリックジャッキング対策**: X-Frame-Options: DENY

### 未実装の推奨セキュリティ機能

- ⚠️ **ブルートフォース攻撃対策**: ログイン試行回数制限
- ⚠️ **2要素認証（2FA）**: TOTP、SMS等
- ⚠️ **パスワード有効期限**: 定期的な変更強制
- ⚠️ **IPホワイトリスト**: 管理画面等の制限
- ⚠️ **監査ログの外部保存**: Splunk、ELK等

---

## 🆔 画面ID・命名規約（Controller / Service / HTML / DTO）

### 1. 画面ID管理体系

#### 基本形式

```
[種別][機能群][サブグループ][機能番号]
  ↓     ↓        ↓           ↓
  V    A        01          01
```

#### 種別（1桁目）

| 記号 | 意味               | 例      |
|------|--------------------|---------|
| **V** | 画面（View）       | VZ0101  |
| **B** | バッチ（Batch）    | BA0101  |
| **A** | API                | AA0101  |

#### 機能群（2桁目）

| 記号 | 分類               | 例      |
|------|--------------------|---------|
| **A** | 機能群A            | VA0101  |
| **B** | 機能群B            | VB0101  |
| **C** | 機能群C            | VC0101  |
| **Z** | 共通機能           | VZ0101  |

#### サブグループ（3～4桁目）

| 記号 | 意味                           | 例      |
|------|--------------------------------|---------|
| **01** | サブグループ1                  | VA0101  |
| **02** | サブグループ2                  | VA0201  |

#### 機能番号（5～6桁目）

| 記号 | 意味                           | 例      |
|------|--------------------------------|---------|
| **01** | 機能1                          | VA0101  |
| **02** | 機能2                          | VA0102  |

#### 画面ID付与ルール

**画面に紐づく要素は必ず画面ID（例: VA0101）を付与する**:

- **HTMLテンプレート**: `va0101.html`（※ `_purchase` のような意味ベースは不要）
- **Controllerクラス**: `VA0101Controller`
- **Form**: `VA0101Form` (画面固有のフォームバインディング)
- **Param**: `VA0101Param` (Controller間のパラメータ受け渡し、必要な場合のみ)

**Service層は意味ベースで命名**(画面IDは不要):

- **命名原則**: ドメインの意味を表す名前にする
- **例**: `ProductService`, `CartService`, `LoginService`, `UserInfoService`
- **理由**: Serviceはドメインロジックを表し、画面に依存しない。再利用性を高める。

**Dto(Data Transfer Object)も意味ベースで命名**:

- **命名原則**: Serviceから返却されるデータの意味を表す名前にする
- **配置**: **Serviceクラスの先頭**にstaticな内部クラスとして定義
- **例**: `UserInfoService.UserInfoDto`, `ProductService.ProductDto`
- **理由**: Serviceと密結合で、パッケージ分離の必要がない。可読性とシンプルさ向上。

**Param(Controller間パラメータ)は画面IDベースで命名**:

- **命名原則**: Controller間のPOSTパラメータ受け渡しに使用
- **配置**: **Controllerクラスの先頭**にstaticな内部クラスとして定義
- **例**: `VA0101Controller.VA0101Param`, `VB0101Controller.VB0101Param`
- **用途**: 画面遷移時のパラメータ受け渡し(FormとParamを分ける場合のみ)
- **理由**: Controllerのメソッド引数と密結合。REST APIのRequestと混同しない。

**FormとDtoとParamの使い分け**:

| クラス種別 | 配置 | 命名規則 | 用途 |
|-----------|------|---------|------|
| **Form** | `controller.{画面ID}` | `{画面ID}Form` | 画面のフォームバインディング・入力値保持 |
| **Dto** | **`{Service}クラス内(先頭)`** | `{意味}Dto` (static内部クラス) | Service層のデータ転送(複数画面で再利用) |
| **Param** | **`{Controller}クラス内(先頭)`** | `{画面ID}Param` (static内部クラス) | Controller間のPOSTパラメータ受け渡し |
| **Request** | **`{RestController}クラス内(先頭)`** | `{API名}Request` (static内部クラス) | REST APIのJSONリクエストボディ |

**実装例**:

```java
// Service層: Dtoをクラスの先頭に定義
@Service
public class UserInfoService {
    
    /**
     * ユーザー情報 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private String userName;
    }
    
    /**
     * 現在ログインしているユーザーの情報を取得する。
     */
    public UserInfoDto getUserInfo() {
        // ユーザー情報取得
        return new UserInfoDto(userName);
    }
}

// Controller: Paramをクラスの先頭に定義(Controller間パラメータ)
@Controller
@RequestMapping("/va0101")
public class VA0101Controller {
    
    /**
     * 商品選択 Param
     * メニュー画面→商品画面への遷移時にパラメータを受け取る
     */
    @Data
    public static class VA0101Param {
        private String productId;
        private String categoryCode;
    }
    
    /**
     * 商品画面 初期表示
     * メニュー画面からPOSTされたパラメータを受け取る
     */
    @GetMapping
    public String init(VA0101Param param, VA0101Form form) {
        // Paramから商品IDを取得して処理
        Product product = productService.getProduct(param.getProductId());
        modelMapper.map(product, form);
        return VIEW;
    }
}

// RestController: Requestをクラスの先頭に定義(REST API用)
@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {
    
    /**
     * 商品登録 Request (REST API)
     */
    @Data
    public static class ProductRequest {
        private String productName;
        private BigDecimal price;
    }
    
    /**
     * 商品を登録する (REST API)
     */
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        // 登録処理
        return ResponseEntity.ok(product);
    }
}

// 通常のWebアプリController: Formのみ使用
@Controller
public class VZ0102Controller {
    public String init(VZ0102Form form) {
        UserInfoDto dto = userInfoService.getUserInfo();
        modelMapper.map(dto, form);  // Dto → Form
        return VIEW;
    }
}
```
        modelMapper.map(dto, form);  // Dto → Form
        return VIEW;
    }
}
```

#### 実装例

| 機能ID | 説明                           | Controller | HTML | Service |
|--------|--------------------------------|------------|------|---------|
| **VZ0101** | ログイン画面 | `VZ0101Controller` | `vz0101.html` | `LoginService` |
| **VZ0102** | メニュー画面 | `VZ0102Controller` | `vz0102.html` | - |
| **VA0101** | 購買画面 | `VA0101Controller` | `va0101.html` | `ProductService` |
| **VB0101** | 顧客一覧画面 | `VB0101Controller` | `vb0101.html` | `CustomerService` |

### 2. Controllerメソッド命名規則

| メソッド種別 | 命名 | 例 | 用途 |
|------------|------|----|----- |
| **初期表示** | `init()` | `init()` | 画面初期表示 |
| **検索系** | `search+対象` | `searchProduct()`, `search()` | データ検索（対象が1つなら `search()` でも可） |
| **一覧選択系** | `select+対象` | `selectProduct()`, `select()` | 一覧から選択（対象が1つなら `select()` でも可） |
| **確定系** | `commit+対象` | `commit()`, `commitCart()` | 確定・更新・登録・追加（対象が1つなら `select()` でも可） |
| **戻る** | `back()` | 前画面へ戻る |
| **画面クリア** | `clear()` | 入力項目クリア |

**確定系の統一**:  
`commit()` を「確定」「更新」に近い意味を持つ共通的なメソッド名として使用します。

```java
// ✅ OK: commit()で統一
public String commit(VA0101Form form, RedirectAttributes redirectAttributes) {
    service.registerProduct(form);
    return "redirect:/menu";
}

public String commitCart(CartForm form, RedirectAttributes redirectAttributes) {
    service.addToCart(form);
    return "redirect:/cart";
}
```

### 3. JavaDoc記載ルール

**Controllerクラスとメソッドには必ず 画面ID＋機能説明 を記載**:

```java
/**
 * VA0101 購買画面 Controller
 */
@Slf4j
@Controller
@RequestMapping("/va0101")
@RequiredArgsConstructor
public class VA0101Controller {
    
    /**
     * VA0101 購買画面 初期表示
     * ユーザーが購買画面を開いた際に商品一覧を表示する
     */
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    @GetMapping
    public String init(Model model) {
        // ...
        return VIEW;
    }
    
    /**
     * VA0101 購買画面 商品検索
     * 検索条件に一致する商品を取得して表示する
     */
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    @PostMapping("/search")
    public String searchProduct(@Validated VA0101Form form, BindingResult result, Model model) {
        // ...
        return VIEW;
    }
}
```

**機能対応表を不要化**:  
コードとJavaDocだけで画面IDと機能内容が直感的に理解できるようにします。

### 4. このルールのメリット

| メリット | 説明 |
|---------|------|
| **クライアント説明に強い** | 画面IDで一覧化可能、顧客との認識合わせが容易 |
| **コード可読性に強い** | メソッドはユースケースベース＋JavaDoc補足で理解しやすい |
| **保守性を確保** | 番号体系＋JavaDocで混乱を防止 |
| **再利用性を確保** | Serviceは意味ベースで共通化 |
| **検索性を確保** | 画面ID・機能名をJavaDocに記載、IDE検索でヒット |

---

## 💻 開発環境

### 必須

- **Java**: 21
- **Maven**: 3.9+
- **PostgreSQL**: 14+ （ポート 5432）
- **IDE**: Eclipse (Pleiades) / IntelliJ IDEA

### データベース初期設定

```sql
-- DB作成（初回のみ）
CREATE DATABASE postgres;
```

#### 接続情報設定（application-pt.yml）

**⚠️ セキュリティ警告**: 本番環境では環境変数またはシークレット管理ツールを使用してください。

```yaml
spring:
  datasource:
    # 環境変数から取得（推奨）
    url: ${DB_URL:jdbc:postgresql://127.0.0.1:5432/postgres}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
```

**環境変数設定例**:

```powershell
# Windows PowerShell
$env:DB_URL="jdbc:postgresql://localhost:5432/postgres"
$env:DB_USERNAME="app_user"
$env:DB_PASSWORD="your_secure_password"
```

```bash
# Linux/Mac
export DB_URL="jdbc:postgresql://localhost:5432/postgres"
export DB_USERNAME="app_user"
export DB_PASSWORD="your_secure_password"
```

**開発環境のみ**: デフォルト値（`postgres`/`postgres`）を使用可能ですが、本番環境では必ず変更してください。

**データベース初期化**:
- `database/init_schema.sql`: テーブル定義、インデックス、制約
- `database/init_data.sql`: 初期データ（開発環境用テストユーザー含む）

**注意**: Spring Boot起動時の自動実行は開発環境のみ推奨。本番環境では手動実行またはマイグレーションツール（Flyway等）を使用してください。

### 環境別設定

本アプリケーションは環境別にプロファイルを使い分けます:

| プロファイル | 用途 | 設定ファイル | データベース |
|------------|------|------------|------------|
| **pt** | 開発環境（PostgreSQL） | `application-pt.yml` | PostgreSQL（localhost） |
| **pr** | 本番環境 | `application-pr.yml` | PostgreSQL（本番DB） |
| **デフォルト** | ローカル開発 | `application.yml` | 共通設定 |

#### 環境別の起動方法

```powershell
# 開発環境（PostgreSQL）
./mvnw spring-boot:run -Dspring-boot.run.profiles=pt

# 本番環境
java -jar -Dspring.profiles.active=pr target/sample-0.0.1-SNAPSHOT.war

# 環境変数で指定
$env:SPRING_PROFILES_ACTIVE="pt"
./mvnw spring-boot:run
```

#### 環境別設定の管理

**開発環境（pt）**:
- デバッグログ有効
- H2コンソール有効（必要に応じて）
- CSRF無効化オプション（開発時のみ）
- テストユーザーアカウント使用可能

**本番環境（pr）**:
- INFOレベル以上のログ
- HTTPS必須
- 環境変数による機密情報管理
- セキュリティ設定の厳格化

```yaml
# application-pr.yml（本番環境）の例
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false

logging:
  level:
    root: INFO
    com.example.sample: INFO

server:
  ssl:
    enabled: true
```

---

## 🚀 ビルド・実行

### Maven ビルド

```powershell
# クリーンビルド
./mvnw clean package

# テストスキップ
./mvnw clean package -DskipTests
```

### アプリケーション起動

```powershell
# プロファイル pt (PostgreSQL) で起動
./mvnw spring-boot:run -Dspring-boot.run.profiles=pt
```

または

```powershell
java -jar -Dspring.profiles.active=pt target/sample-0.0.1-SNAPSHOT.war
```

### アクセス

- **URL**: http://localhost:8080/vz0101
- **ログインユーザー**:
  - **開発環境のみ**: `data.sql` に定義されたテストユーザーでログイン可能
  - **本番環境**: 初期パスワードは管理者から別途取得してください
  - **パスワードポリシー**: 8文字以上、英数字記号混在を推奨

**⚠️ セキュリティ警告**: 
- デフォルトパスワードは開発環境専用です
- 本番環境では必ず強固なパスワードに変更してください
- 初回ログイン後、パスワード変更を強制することを推奨します

---

## 📦 主要な依存関係

| 機能                   | ライブラリ                            |
|------------------------|---------------------------------------|
| Web フレームワーク     | Spring Boot Starter Web               |
| セキュリティ           | Spring Boot Starter Security          |
| テンプレートエンジン   | Thymeleaf + Thymeleaf Extras Security |
| ORM                    | MyBatis Spring Boot Starter           |
| AOP                    | Spring Boot Starter AOP               |
| バリデーション         | Spring Boot Starter Validation        |
| データベース           | PostgreSQL JDBC Driver                |
| ユーティリティ         | Lombok                                |

---

## 📝 コーディング規約

### Controller

**必須アノテーション**:
```java
@Slf4j                      // ログ出力用
@Controller                 // Spring MVCコントローラー
@RequestMapping("/vz0101")  // ベースパス
@RequiredArgsConstructor    // finalフィールドのDI
```

**必須定数**:
```java
public class VA0101Controller {
    // パス定義（リダイレクトで使用）
    public static final String PATH = "/va0101";
    
    // VIEW名定義（Thymeleafテンプレートパス）
    public static final String VIEW = "pages/va0101/index";
    
    // リダイレクト先定義
    public static final String REDIRECT = "redirect:" + PATH;
    
    // フォーム名定義（ModelAttributeとHTMLで統一）
    public static final String FORM = "va0101Form";
}
```

**フォーム名の命名規則**:
- 形式: `{画面ID小文字}Form` (例: `va0101Form`, `vb0101Form`, `vz0102Form`)
- キャメルケース(先頭小文字)で統一
- HTML側の `th:object` とController側で同一の名前を使用
- Springが自動的にFormクラス名から推測してModelに追加

**@ModelAttributeの省略**:
```java
// ❌ NG: @ModelAttributeでフォーム名を明記（不要）
public String init(@ModelAttribute("va0101Form") VA0101Form form, Model model) { ... }

// ✅ OK: @ModelAttributeは省略（自動でクラス名から推測される）
public String init(VA0101Form form, Model model) { ... }

// ✅ 推奨: @ModelAttributeは省略（Springが自動でクラス名から推測）
public String init(VA0101Form form) { ... }

// ✅ OK: セッションスコープで保持する場合のみ@SessionAttributesを明記
@Controller
@SessionAttributes("va0101Form")  // セッションに保持
public class VA0101Controller { ... }
```

**ModelMapperによるDTO/Entityマッピング**:
```java
@RequiredArgsConstructor
public class VA0101Controller {
    private final ModelMapper modelMapper;  // DIで注入
    
    // ✅ 推奨: ModelMapperで明示的にマッピング
    @GetMapping
    public String init(VA0101Form form) {
        // Service層からDTOを取得
        ProductDto productDto = productService.getProduct();
        
        // ModelMapperでFormにマッピング
        modelMapper.map(productDto, form);
        
        return VIEW;
    }
    
    // ❌ 非推奨: 手動でsetterを呼び出し（保守性が低い）
    @GetMapping
    public String init(VA0101Form form) {
        ProductDto productDto = productService.getProduct();
        form.setProductName(productDto.getName());
        form.setProductCode(productDto.getCode());
        form.setPrice(productDto.getPrice());
        // ... 多数のsetterが必要
        return VIEW;
    }
}
```

**ModelMapperのBean定義**:
```java
// WebMvcConfig.java
@Bean
public ModelMapper modelMapper() {
    return new ModelMapper();
}
```

**pom.xmlへの依存関係追加**:
```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.2.1</version>
</dependency>
```

**Model引数の使い分け**:
```java
// ❌ NG: Formのみを扱う場合にModel引数は不要
public String init(VA0101Form form, Model model) {
    // Formは自動的にModelに追加されるため、Model引数不要
    return VIEW;
}

// ✅ OK: Formのみを扱う場合はModel引数を省略
public String init(VA0101Form form) {
    // Springが自動的にFormをModelに追加
    return VIEW;
}

// ✅ OK: メッセージ表示はModel引数を使用
public String search(VA0101Form form, Model model) {
    List<Product> products = productService.search(form);
    model.addAttribute("message", "検索完了");  // メッセージを追加
    return VIEW;
}
```

**HTMLテンプレートでのth:object配置**:
```html
<!-- ❌ NG: th:objectをdivに配置（バインディングエラーの原因） -->
<div th:object="${va0101Form}">
    <form method="post" th:action="@{/}">
        <input type="text" th:field="*{productName}">
    </form>
</div>

<!-- ✅ OK: th:objectをformタグに配置 -->
<form method="post" th:action="@{/}" th:object="${va0101Form}">
    <input type="text" th:field="*{productName}">
    <input type="text" th:field="*{productCode}">
</form>
```

**メソッド内のログ出力**:
```java
// ❌ NG: メソッド開始ログは不要（InvocationLoggingAspectで自動出力）
public String init(VA0101Form form, Model model) {
    log.debug("VA0101 商品検索画面 初期表示");  // 不要
    return VIEW;
}

// ✅ OK: AOPで自動出力されるため省略
public String init(VA0101Form form, Model model) {
    // 処理のみ記載
    return VIEW;
}

// ✅ OK: 業務ロジック内の重要な判断やエラーはログ出力
public String search(VA0101Form form, Model model) {
    if (form.getKeyword() == null) {
        log.warn("検索キーワードが未入力です");
    }
    // ...
    return VIEW;
}
```

**@ActivateTokenの必須化とアノテーション順序**:
```java
// ✅ 全画面のGETメソッドに@ActivateToken(type=CREATE)が必須
// アノテーション順序: @GetMapping → @ActivateToken
@GetMapping
@ActivateToken(type = ActivateToken.TokenType.CREATE)
public String init(VA0101Form form, Model model) {
    return VIEW;
}

// ✅ 全画面のPOSTメソッドに@ActivateToken(type=VALIDATE)が必須
// アノテーション順序: @PostMapping → @ActivateToken
@PostMapping("/search")
@ActivateToken(type = ActivateToken.TokenType.VALIDATE)
public String search(@Validated VA0101Form form, BindingResult result, Model model) {
    if (result.hasErrors()) {
        return VIEW;
    }
    // ...
    return VIEW;
}
```

**アノテーション記載順序の統一**:
- Mappingアノテーション（@GetMapping/@PostMapping）を先に記載
- その後に@ActivateTokenを記載
- この順序により、HTTPメソッドとパスが視覚的に明確になる

**例外処理**:
```java
/**
 * Service層からスローされたServiceExceptionを捕捉
 * エラーメッセージを設定して自画面に戻る
 */
@ExceptionHandler(ServiceException.class)
public String handleServiceException(ServiceException ex, Model model) {
    log.warn("業務例外発生: code={}, message={}", ex.getErrorCode(), ex.getMessage());
    model.addAttribute("errorMessage", ex.getMessage());
    return VIEW;
}
```

**ParamとFormの使い分け実例**:
```java
/**
 * VA0101 商品検索画面 Controller
 */
@Controller
@RequestMapping(VA0101Controller.PATH)
public class VA0101Controller {
    
    /**
     * 商品選択パラメータ
     * メニュー画面→商品画面への遷移時に使用
     */
    @Data
    public static class VA0101Param {
        /** 選択された商品ID */
        private String selectedProductId;
        /** カテゴリコード */
        private String categoryCode;
    }
    
    // 定数定義...
    
    /**
     * 初期表示: 他画面からPOSTで遷移してきた場合、Paramで値を受け取る
     * 
     * @param param 遷移元から受け取るパラメータ(Paramがある場合)
     * @param form 画面のForm(Springが自動生成)
     */
    @GetMapping
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    public String init(VA0101Param param, VA0101Form form) {
        // Paramに値がある場合は初期値として設定
        if (param.getSelectedProductId() != null) {
            form.setProductId(param.getSelectedProductId());
        }
        return VIEW;
    }
    
    /**
     * 検索: 画面内の操作なのでFormのみ使用
     */
    @PostMapping("/search")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String search(@Validated VA0101Form form, BindingResult result) {
        if (result.hasErrors()) {
            return VIEW;
        }
        // Formの値で検索実行
        List<Product> products = service.search(form);
        return VIEW;
    }
}

// メニュー画面から商品画面への遷移例
@Controller
@RequestMapping("/vz0102")
public class VZ0102Controller {
    @PostMapping("/selectProduct")
    public String selectProduct(@RequestParam String productId) {
        // リダイレクト時にパラメータを付与
        return "redirect:/va0101?selectedProductId=" + productId;
    }
}
```

**命名規則**:
- クラス名: `{ScreenId}Controller` (例: `VZ0101Controller`)
- メソッド名: 動詞形 (`index`, `search`, `update`, `delete`)
- パラメータ名: camelCase (例: `redirectAttributes`)

### Service

**必須アノテーション**:
```java
@Slf4j                      // ログ出力用
@Service                    // Springサービス
@RequiredArgsConstructor    // finalフィールドのDI
```

**例外スロー**:
```java
public class VA0101Service {
    private final MessageSource messageSource;
    
    public void updateProduct(ProductForm form) {
        // 業務ロジックエラー
        if (!isValidProduct(form)) {
            String message = messageSource.getMessage(
                "E_VA0101_001", 
                new Object[]{form.getProductId()}, 
                null
            );
            throw new ServiceException("E_VA0101_001", message);
        }
        
        // データ更新処理
        // ...
    }
}
```

**トランザクション**:
```java
@Transactional  // データ更新処理に必須
public void updateProduct(ProductForm form) {
    // 複数テーブルへの更新が一貫性を保つ
    productMapper.update(form);
    stockMapper.updateQuantity(form.getProductId(), form.getQuantity());
}

@Transactional(readOnly = true)  // 検索処理は読み取り専用
public List<Product> search(SearchForm form) {
    return productMapper.selectByCondition(form);
}
```

### Form

**クラスコメント**:
```java
/**
 * 商品検索 Form
 * 検索条件を保持する
 */
@Data
public class VA0101Form implements Serializable {
    // ...
}
```

**バリデーション**:
```java
@NotBlank(message = "商品名は必須です")
@Size(max = 100, message = "商品名は100文字以内で入力してください")
private String productName;

@Min(value = 0, message = "価格は0以上で入力してください")
@Max(value = 9999999, message = "価格は9,999,999以下で入力してください")
private BigDecimal price;

@Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", 
         message = "日付はYYYY-MM-DD形式で入力してください")
private String registeredDate;
```

### Mapper (MyBatis)

**インターフェース**:
```java
@Mapper
public interface ProductMapper {
    /**
     * 商品情報を登録する
     * @param product 商品情報
     * @return 登録件数
     */
    int insert(ProductEntity product);
    
    /**
     * 商品情報を更新する
     * @param product 商品情報
     * @return 更新件数
     */
    int update(ProductEntity product);
    
    /**
     * 商品情報を検索する
     * @param condition 検索条件
     * @return 商品リスト
     */
    List<ProductEntity> selectByCondition(SearchForm condition);
}
```

**XMLマッピング**:
```xml
<!-- SQLインジェクション対策: #{を使用（PreparedStatement） -->
<select id="selectByCondition" resultType="ProductEntity">
    SELECT
        PRODUCT_ID,
        PRODUCT_NAME,
        PRICE,
        STOCK_QUANTITY
    FROM SAMPLE.TM_PRODUCT
    WHERE DEL_FLG = FALSE
    <if test="productName != null and productName != ''">
        AND PRODUCT_NAME LIKE CONCAT('%', #{productName}, '%')
    </if>
    <if test="minPrice != null">
        AND PRICE >= #{minPrice}
    </if>
    ORDER BY PRODUCT_ID
</select>
```

### ログ

**日本語メッセージ必須**:
```java
// ❌NG: 英語メッセージ
log.info("User logged in: {}", userId);

// ✅OK: 日本語メッセージ
log.info("ログイン成功: userId={}", userId);
```

**ログレベルの使い分け**:
```java
// DEBUG: デバッグ情報（開発環境のみ）
log.debug("検索条件: {}", searchForm);

// INFO: 業務イベント
log.info("商品登録完了: productId={}", productId);

// WARN: 警告（処理継続可能）
log.warn("在庫不足: productId={}, requested={}, stock={}", 
    productId, requestedQty, stockQty);

// ERROR: エラー（処理中断）
log.error("データベース接続失敗", exception);
```

### フロントエンド実装規約（Thymeleaf / Bootstrap）

#### 使用技術

- **Thymeleaf**: テンプレートエンジン
- **Bootstrap 5**: UIフレームワーク（カスタムCSS最小化）

#### 画面構造の基本原則

**1画面1フォーム構造**:

```html
<!-- ✅ 基本構造: 全体をフォームで囲む -->
<div layout:fragment="content">
  <form id="{画面ID小文字}Form" th:object="${フォーム名}" method="post">
    <!-- 画面コンテンツ全体 -->
    
    <!-- 各ボタンはth:formactionでアクション指定 -->
    <button type="submit" class="btn btn-primary" th:formaction="@{/path/to/action}">実行</button>
  </form>
</div>

<!-- 例: VZ0102メニュー画面 -->
<form id="vz0102Form" th:object="${vz0102Form}" method="post">
  <!-- メニューボタン群 -->
  <button type="submit" class="btn btn-primary" th:formaction="@{/va0101}">購買画面</button>
  <button type="submit" class="btn btn-primary" th:formaction="@{/vb0101}">顧客一覧</button>
</form>
```

**フォーム要素の必須属性**:

| 属性 | 必須 | 説明 | 例 |
|------|-----|------|----|
| **id** | ✅ | フォームID（`{画面ID小文字}Form`） | `id="vz0102Form"` |
| **th:object** | ✅ | バインドするフォームオブジェクト | `th:object="${vz0102Form}"` |
| **method** | ✅ | HTTPメソッド（通常はPOST） | `method="post"` |
| **th:action** | ⚠️ | 単一アクションの場合のみ指定 | `th:action="@{/va0101/search}"` |

**ボタンごとのアクション指定**:

```html
<!-- ✅ 1画面1フォーム: 各ボタンにth:formactionを指定 -->
<button type="submit" class="btn btn-primary" th:formaction="@{/va0101/search}">検索</button>
<button type="submit" class="btn btn-secondary" th:formaction="@{/va0101/clear}">クリア</button>
<button type="submit" class="btn btn-success" th:formaction="@{/va0101/commit}">確定</button>

<!-- ❌ NG: th:actionだと全ボタンが同じアクションになる -->
<form th:action="@{/va0101/search}" method="post">
  <button type="submit" class="btn btn-primary">検索</button>
  <button type="submit" class="btn btn-secondary">クリア</button> <!-- ❌ /searchに飛ぶ -->
</form>
```

#### PRGパターン（Post-Redirect-Get）

**データ更新処理は必ずリダイレクトを使用**:

```java
// ✅ 登録・更新・削除処理: POST → Redirect → GET
@PostMapping("/commit")
public String commit(@Validated VA0101Form form, RedirectAttributes redirectAttributes) {
    service.register(form);
    message.addFlashMessage(redirectAttributes, "I0001", new Object[]{"登録"});
    return "redirect:/vz0102";  // メニュー画面へリダイレクト
}

// ✅ 検索処理: POST → VIEW直接表示
@PostMapping("/search")
public String search(@Validated VA0101Form form, Model model) {
    List<Product> products = service.search(form);
    model.addAttribute("products", products);
    return VIEW;  // 検索結果を直接表示
}
```

**PRGパターンのメリット**:
- ブラウザの戻るボタン対策（戻る→進むで再POST防止）
- F5キー（リロード）による二重送信防止
- URLがGETエンドポイントになる（ブックマーク・共有可能）

#### Bootstrap中心の実装

**カスタムCSSは最小限に**:

```css
/* style.css: カスタムCSSは必要最小限のみ */

/* ログイン画面専用の背景グラデーション */
body.login-page {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* その他はBootstrapユーティリティクラスで対応 */
```

**Bootstrapクラスの活用例**:

```html
<!-- ✅ レイアウト: Bootstrapグリッド -->
<div class="row g-3">
  <div class="col-12 col-md-6 col-lg-4">
    <button type="submit" class="btn btn-primary w-100">メニュー</button>
  </div>
</div>

<!-- ✅ スペーシング: Bootstrapユーティリティ -->
<div class="container" style="max-width: 1200px;">
  <div class="my-4">  <!-- margin-top: 1.5rem, margin-bottom: 1.5rem -->
    <h1 class="mb-3">タイトル</h1>  <!-- margin-bottom: 1rem -->
  </div>
</div>

<!-- ✅ カード: Bootstrap標準コンポーネント -->
<div class="card border-0 shadow-sm mb-4">
  <div class="card-body p-4 bg-light">
    <h2 class="card-title">セクション</h2>
  </div>
</div>

<!-- ❌ NG: カスタムCSSで実装（不要） -->
<style>
  .custom-card {
    border: none;
    box-shadow: 0 0.125rem 0.25rem rgba(0,0,0,0.075);
    margin-bottom: 1.5rem;
  }
</style>
```

#### 実装ルール

**原則として個別の JavaScript / CSS 実装は禁止**:

```html
<!-- ✅ OK: Bootstrap標準機能を使用 -->
<button type="submit" class="btn btn-primary">検索</button>

<!-- ✅ OK: Thymeleaf属性で条件分岐 -->
<div th:if="${errorMessage}" class="alert alert-danger" th:text="${errorMessage}"></div>

<!-- ❌ NG: 個別JavaScript実装（例外事由なし） -->
<script>
  document.getElementById('searchBtn').addEventListener('click', function() {
    // カスタム処理...
  });
</script>
```

**例外事由**:

顧客要望により、標準機能では対応できないケースに限り個別実装を許可します:

```javascript
/**
 * [例外理由] 顧客要望: リアルタイム入力補完機能
 * Bootstrap標準では対応不可のため、個別実装
 * 影響範囲: 商品検索画面（VA0101）のみ
 */
function initAutocomplete() {
    // ...
}
```

#### 運用上の注意

**個別実装を行う場合**:

1. **共通化・再利用性を意識**:
   - 共通JS/CSSファイルにまとめる（例: `static/js/common-custom.js`）
   - 画面単位で散在させない

2. **透明性確保**:
   - README.mdや設計書に「例外理由」を記載
   - 顧客説明時には「標準機能で対応できないため個別実装」と明示

3. **動作検証の徹底**:
   - 個別実装部分は単体テスト・結合テストで重点的に検証
   - ブラウザ互換性チェック（Chrome/Edge/Safari）

#### このルールのメリット

| メリット | 説明 |
|---------|------|
| **保守性向上** | 標準機能に依存することで、後続開発や修正が容易 |
| **透明性確保** | 顧客に「標準で対応できる／できない」を明示できる |
| **品質安定** | 個別実装を最小化することで、動作検証やバグリスクを減らせる |
| **学習コスト低減** | 新規参画者がBootstrap/Thymeleafのみ習得すればよい |

### 命名規則まとめ

| 種別 | 規則 | 例 |
|------|------|----|
| **パッケージ** | 小文字のみ | `com.example.sample.controller` |
| **クラス** | PascalCase | `ProductService`, `VA0101Controller` |
| **メソッド** | camelCase | `searchProduct`, `updateStock`, `commit` |
| **定数** | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT` |
| **変数** | camelCase | `productId`, `userName` |
| **プライベートフィールド** | camelCase | `private String productName;` |
| **HTMLファイル** | 画面ID小文字 | `va0101.html`, `vz0101.html` |

---

## 🛡 セキュリティ機能

- ✅ CSRF 保護（Spring Security）
- ✅ 二重送信防止トークン（カスタム実装）
- ✅ BCrypt パスワードハッシュ（strength=12）
- ✅ セッション固定攻撃対策（ログイン時 ID 再生成）
- ✅ 同時ログインセッション数制限（最大1）
- ✅ 認証失敗時の統一エラーメッセージ
- ✅ ブラウザ制御（F5/Ctrl+R/Backspace/右クリック禁止）

---

## 📚 参考情報

- [Spring Boot 公式ドキュメント](https://spring.io/projects/spring-boot)
- [MyBatis Spring Boot Starter](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL 公式ドキュメント](https://www.postgresql.org/docs/)

---

## 🔧 トラブルシューティング

### アプリケーション起動失敗

#### 1. データベース接続エラー

**エラーメッセージ**:
```
Caused by: org.postgresql.util.PSQLException: Connection refused
```

**原因と対策**:
1. PostgreSQLが起動していない
   ```powershell
   # WindowsでPostgreSQLサービス確認
   Get-Service -Name postgresql*
   
   # サービス起動
   Start-Service -Name postgresql-x64-14
   ```

2. 接続情報が間違っている
   ```yaml
   # application-pt.yml を確認
   spring:
     datasource:
       url: jdbc:postgresql://127.0.0.1:5432/postgres
       username: postgres
       password: postgres
   ```

3. データベースが存在しない
   ```sql
   -- psqlで確認
   \l
   
   -- データベース作成
   CREATE DATABASE postgres;
   ```

#### 2. ポート競合エラー

**エラーメッセージ**:
```
Web server failed to start. Port 8080 was already in use.
```

**対策**:
```powershell
# ポート8使用中のプロセス確認
netstat -ano | findstr :8080

# プロセス終了（PIDを指定）
taskkill /PID <PID> /F

# またはポート変更
# application.yml
server:
  port: 8081
```

### ログインエラー

#### 1. 認証失敗

**エラー**: 「ユーザー名またはパスワードが間違っています」

**確認事項**:
1. テストユーザーが登録されているか
   ```sql
   SELECT USER_ID, USER_NAME, ROLE FROM SAMPLE.TM_USER;
   ```

2. パスワードが正しくハッシュ化されているか
   ```sql
   -- パスワード検証（開発環境のみ）
   SELECT 
       USER_ID, 
       PASSWORD = crypt('password123', PASSWORD) AS password_match 
   FROM SAMPLE.TM_USER 
   WHERE USER_ID = 'user01';
   ```

3. アカウントがロックされていないか
   ```sql
   SELECT USER_ID, ACCOUNT_LOCKED FROM SAMPLE.TM_USER WHERE USER_ID = 'user01';
   ```

#### 2. CSRFトークンエラー

**エラー**: 403 Forbidden

**原因**: フォームにCSRFトークンが含まれていない

**対策**:
```html
<!-- Thymeleafでth:actionを使用（自動でCSRFトークン挿入） -->
<form th:action="@{/vz0101/login}" method="post">
    <!-- フォームフィールド -->
</form>
```

### 二重送信防止トークンエラー

**エラー**: 「不正なトークンです」

**原因**:
1. トークンがセッションに存在しない
2. POST処理後にトークンが再生成されていない

**対策**:
```java
// GETメソッドに@ActivateToken(type=CREATE)を付与
@ActivateToken(type = ActivateToken.TokenType.CREATE)
@GetMapping
public String index(Model model) {
    return VIEW;
}

// POSTメソッドに@ActivateToken(type=VALIDATE)を付与
@ActivateToken(type = ActivateToken.TokenType.VALIDATE)
@PostMapping("/execute")
public String execute(Form form, RedirectAttributes redirectAttributes) {
    // 検証後、自動で新トークン生成
    return "redirect:/menu";
}
```

### MyBatisエラー

#### 1. Mapperが見つからない

**エラー**:
```
org.apache.ibatis.binding.BindingException: Invalid bound statement (not found)
```

**原因と対策**:
1. XMLファイルの配置場所が間違っている
   - `src/main/resources/mapper/` に配置する

2. namespaceが間違っている
   ```xml
   <!-- XMLのnamespaceとJavaインターフェースの完全修飾名を一致させる -->
   <mapper namespace="com.example.sample.mapper.ProductMapper">
   ```

3. メソッド名がidと一致していない
   ```xml
   <!-- Java: List<Product> selectAll() -->
   <select id="selectAll" resultType="Product">
   ```

---

## ⚡ パフォーマンスチューニング

### データベース最適化

#### 1. インデックスの作成

```sql
-- 頻繁に検索されるカラムにインデックスを作成
CREATE INDEX idx_product_name ON SAMPLE.TM_PRODUCT(PRODUCT_NAME);
CREATE INDEX idx_product_company ON SAMPLE.TM_PRODUCT(COMPANY_ID);

-- 複合インデックス（複数カラムで検索する場合）
CREATE INDEX idx_product_search 
    ON SAMPLE.TM_PRODUCT(COMPANY_ID, PRODUCT_NAME, DEL_FLG);
```

#### 2. N+1問題の回避

```xml
<!-- ❌NG: ループ内でSQL発行 -->
<select id="selectProduct" resultType="Product">
    SELECT * FROM TM_PRODUCT WHERE PRODUCT_ID = #{productId}
</select>

<!-- ✅OK: JOINで一括取得 -->
<select id="selectProductsWithCompany" resultMap="ProductResultMap">
    SELECT 
        p.PRODUCT_ID,
        p.PRODUCT_NAME,
        c.COMPANY_ID,
        c.COMPANY_NAME
    FROM SAMPLE.TM_PRODUCT p
    LEFT JOIN SAMPLE.TM_COMPANY c ON p.COMPANY_ID = c.COMPANY_ID
    WHERE p.DEL_FLG = FALSE
</select>
```

#### 3. ページネーション

```xml
<!-- 大量データはページングで取得 -->
<select id="selectByPage" resultType="Product">
    SELECT * FROM SAMPLE.TM_PRODUCT
    WHERE DEL_FLG = FALSE
    ORDER BY PRODUCT_ID
    LIMIT #{pageSize} OFFSET #{offset}
</select>
```

### キャッシュ設定

```java
// Spring Cacheを使用（頻繁に参照されるマスタデータ）
@Cacheable(value = "products", key = "#productId")
public Product getProduct(String productId) {
    return productMapper.selectById(productId);
}

@CacheEvict(value = "products", key = "#product.productId")
public void updateProduct(Product product) {
    productMapper.update(product);
}
```

```yaml
# application.yml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=10m
```

### セッション最適化

```yaml
# application.yml
server:
  servlet:
    session:
      timeout: 30m  # タイムアウトを適切に設定
      cookie:
        max-age: 1800  # 30分
```

### ログレベル調整

```yaml
# application-pr.yml（本番環境）
logging:
  level:
    root: INFO
    com.example.sample: INFO
    org.springframework.web: WARN
    org.hibernate.SQL: WARN  # SQLログを抑制
```

### 推奨設定

```yaml
# application-pr.yml
spring:
  jpa:
    show-sql: false  # SQLログを無効化
  thymeleaf:
    cache: true  # テンプレートキャッシュ有効化

server:
  compression:
    enabled: true  # gzip圧縮有効化
    mime-types: text/html,text/css,application/javascript,application/json
  http2:
    enabled: true  # HTTP/2有効化
```

---

**Last Updated**: 2025年12月3日
