# Jserver

A lightweight Java Web Server, designed to simplify the process of launching a simple web page.

## About

**Jserver** is a minimalistic web server written in Java. It aims to provide a straightforward and efficient way to serve web pages without the need for complex configurations or dependencies.

## Features

- Lightweight and easy to set up
- Supports basic HTTP GET and POST requests
- Customizable root path for serving files
- Simple error handling for common HTTP status codes

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven

### Installation

1. Clone the repository:
```bash
git clone https://github.com/EtHShAwN/Jserver.git
cd Jserver
```
2. Build the project using Maven:
  ```bash
  mvn clean install
  ```
3. Build as package
```bash
mvn clean package
```
### Usage
#### Run the server:
  ```bash
  java -jar target/Jserver-1.0-SNAPSHOT.jar
  ```
  Access the server in your web browser at http://localhost:80 by default.

### Configuration
You can configure the server by modifying the config files located in the config directory. For example, you can set the root path for serving files or customize error pages.

### Contributing
Contributions are welcome! Please fork the repository and submit a pull request.

### License
This project is licensed under the Apache-2.0 License - see the LICENSE file for details.

### Authors
EtHShAwN
Karapeno
