<img src=".github/resources/logo.jpg" />

<h1 align="center">Mediatr ⚡ for Spring Boot 3 + Java 17+</h1>

<p align="center">
Lightweight, dependency-free in-process messaging. <br/>
Designed for clean, maintainable architectures with modern Java.
</p>

<p align="center">
  <a href="https://search.maven.org/artifact/io.github.admiralxy/spring-boot-starter-mediatr">
    <img src="https://img.shields.io/maven-central/v/io.github.admiralxy/spring-boot-starter-mediatr?style=for-the-badge" alt="Maven Central Version"/>
  </a>
  <img src="https://img.shields.io/github/actions/workflow/status/AdmiralXy/spring-mediatr/build.yml?style=for-the-badge" alt="GitHub Actions Workflow Status"/>
  <img src="https://img.shields.io/github/license/AdmiralXy/spring-mediatr?style=for-the-badge" alt="GitHub License"/>
</p>

<br clear="both"/>

## ✨ What is Mediatr?

Lightweight, dependency-free in-process messaging.

Designed for clean, maintainable architectures with modern Java.

|                                      | Mediatr‑Java‑Spring | Spring Events | Reactor | Manual wiring |
|--------------------------------------|---------------------|---------------|---------|---------------|
| 🔌 Auto‑wiring                       | ✅                   | ⚠️ limited    | —       | —             |
| 🌀 Request / Notification separation | ✅                   | ❌             | ✅       | ⚠️ manual     |
| 🔄 Pipeline behaviors (Retry, Tx…)   | ✅                   | ❌             | ❌       | ⚠️ manual     |
| 🧵 Virtual‑thread friendly           | ✅                   | ⚠️            | ✅       | ⚠️            |
| 🛠 DX‑first (single starter)         | ✅                   | ❌             | ⚠️      | ❌             |

Stop reinventing the wheel — start shipping features! ⭐

---

## 🚀 Quick Start with Spring Boot 3.0+

### 1️⃣ Add dependency



```xml
# Gradle
implementation 'io.github.admiralxy:spring-boot-starter-mediatr:<latest release>'

# Maven
<dependency>
    <groupId>io.github.admiralxy</groupId>
    <artifactId>spring-boot-starter-mediatr</artifactId>
    <version><!-- latest release --></version>
</dependency>
```

### 2️⃣ Define a request

```java
public record GetUserQuery(UUID id) implements Request<UserDto> {}
```

### 3️⃣ Create a handler

```java
@Handler
class GetUserHandler implements RequestHandler<GetUserQuery, UserDto> {
    public UserDto handle(GetUserQuery query) {
        return new UserDto(...);
    }
}
```

### 4️⃣ Trigger the request

```java
@Autowired Mediator mediator;

UserDto dto = mediator.send(new GetUserQuery(userId));
```

## 📣 Notifications example

```java
public record OrderPlaced(UUID id) implements Notification {}

@Handler
class SendEmail implements NotificationHandler<OrderPlaced> { /* … */ }

@Handler
class EnqueueWebhook implements NotificationHandler<OrderPlaced> { /* … */ }

@Autowired
private Mediator mediator;

mediator.publish(new OrderPlaced(orderId)); // fan‑out to *all* handlers
```

## ⚙️ Configuration
You can tweak the mediator via Spring Boot `application.yml / application.properties`:
```xml
# ⚙️ Thread pool configuration (ignored when virtual threads are enabled)
mediator.core-pool: 8          # Core thread pool size (default: availableProcessors)
mediator.max-pool: 16          # Max thread pool size (default: 2 × availableProcessors)
mediator.keep-alive: 60s       # Idle thread keep-alive time (default: 60s)
mediator.queue-capacity: 10000 # Task queue size (default: 10000)

# 🧵 Enable virtual threads (requires JDK 21+; default: true)
mediator.virtual-threads: true
```

## 🌍 Roadmap
* 📊 **Micrometer metrics**
* 🛡 **`@Transactional` samples**
* 🧪 **Test‑kit & MockMediator**

Give the project a ⭐ if you like the vision — it fuels development!

## 🤝 Contributing
1. **Fork** → `git clone` → feature branch.
2. `./gradlew build` (JDK 17+).
3. Open **PR** with green CI.

Questions? Create an issue or join discussions!

## 📜 License
**Apache License 2.0** — free for personal & commercial use.

> Built with ☕ & ❤️ by [@AdmiralXy](https://github.com/AdmiralXy) — Happy coding!
