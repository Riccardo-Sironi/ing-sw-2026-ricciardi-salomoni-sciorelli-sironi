# Mesos - Software Engineering Final Project 2025/2026

<div align="center">
  <img src="https://img.shields.io/badge/Java-25-blue" alt="Java"/>
</div>
<div align="center">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/JavaFX-FF0000?style=for-the-badge&logo=openjdk&logoColor=white" alt="JavaFX"/>
</div>

<div>

<img alt="Codex Naturalis game logo" src="/src/main/resources/imgs/readme/mesos%20box.webp" width=300px height=300 px align="right" />

This repository contains a full digital adaptation of the board game **Mesos**, developed entirely in Java using Maven
and JavaFX. The project implements a robust Client-Server architecture allowing multiple players to connect and play
together over a network.

The codebase has been designed from scratch following strict **SOLID** principles, clean code guidelines, and
*OOP/Functional* paradigms. The structural backbone of the application relies on the **MVC (Model-View-Controller)**
architectural pattern, enriched with standard GoF design patterns (Factory Method, Observer, State, Command, etc.) to
ensure high cohesion and low coupling.

</div>

# Implemented Features

| Feature              | Status | Description                                                                                                                           |
|:---------------------|:------:|:--------------------------------------------------------------------------------------------------------------------------------------|
| **Basic Rules**      |   🟢   | Complete implementation of the official Mesos board game rules.                                                                       |
| **CLI / TUI**        |   🟢   | Command Line Interface leveraging ANSI escape codes and UTF-8 for a clean terminal experience.                                        |
| **GUI**              |   🟢   | Graphical User Interface built with JavaFX, featuring dynamic animations and responsive layouts.                                      |
| **TCP + RMI**        |   🟢   | Full TCP Socket and RMI implementation for client-server communication, allowing clients with different technologies to play together |
| **Multiple Matches** |   🟢   | Server capable of handling multiple distinct matches and lobbies concurrently.                                                        |
| **Persistence**      |   🟢   | Server-side persistance on crashes, allowing players to restart from where they had left                                              |
| **Database**         |   🟢   | Seamless database integration for a full leaderboard of the matches                                                                   |

# Server configuration

* Run the .jar file for the server with the following command ```java -jar MesosServer.jar```;

**IMPORTANT** : If you aim to play with different devices over the net, you can achieve this by using a third-party
software such
as [Hamachi](https://vpn.net/), [ZeroTier](https://www.zerotier.com/), [Wireguard](https://www.wireguard.com/) or
properly set
forward rules on the router where the
server is located.

Remember to launch the jar with the command
```java --ip=SERVER_PUBLIC_IP_ADDRESS -jar MesosServer.jar```

## Available arguments

| Argument      | Description                                                                   |
|:--------------|:------------------------------------------------------------------------------|
| `--ip`        | Specifies the IP address to bind the server to.                               |
| `--dname`     | Specifies the name of the database to connect to.                             |
| `--dloc`      | Specifies the location of the database.                                       |
| `--duser`     | Specifies the database user credentials in the format `username:password   `. |
| `--tcp`       | Specifies the TCP port number for client connections (default: 45161).        |
| `--rmi`       | Specifies the RMI port number for client connections (default: 1099).         |
| `--rmiexport` | Specifies the RMI export port number for client connections (default: 1100).  |

# Client configuration

* Run the .jar inside your own operating systems' **Command Prompt** for the client with the following command
  ```java -jar MesosClient.jar```
* Follow the instructions that appear on the screen to start a game (make sure to enter the correct Server IP).

**IMPORTANT** : If you aim to play with different devices over the net, you can achieve this by using a third-party
software such
as [Hamachi](https://vpn.net/), [ZeroTier](https://www.zerotier.com/), [Wireguard](https://www.wireguard.com/) or
properly set
forward rules on the router where the
server is located.

In case you want to play with RMI over the Internet, remember to launch the jar with the command
```java --ip=YOUR_PUBLIC_IP_ADDRESS -jar MesosClient.jar```

# Group GC06

- Manuel Ricciardi  : manuel.ricciardi@mail.polimi.it
- Tommaso Salomoni  : tommaso.salomoni@mail.polimi.it
- Filippo Sciorelli : filippo.sciorelli@mail.polimi.it
- Riccardo Sironi   : riccardo1.sironi@mail.polimi.it