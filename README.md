# Capital Gains Tax Calculator

Este projeto é um sistema para cálculo de imposto sobre ganhos de capital em operações de compra e venda de ações. Ele processa entradas JSON contendo operações e retorna os impostos devidos conforme as regras fiscais estabelecidas.

---

## 🚀 Como Executar o Projeto

### 1️⃣ Pré-requisitos
- **Java 23** (Certifique-se de que está instalado e configurado corretamente)
- **Maven** (Utilizado para gerenciar dependências e build)

### 2️⃣ Compilar o projeto
```sh

cd capital-gains-nu

mvn clean package
```
### 3️⃣ Executar o programa
```sh
java -jar target/capitalGainsNu-1.0-SNAPSHOT.jar < arquivo.json

java -jar target/capitalGainsNu-1.0-SNAPSHOT.jar

```

## 🏗️ Estrutura do projeto
```bash
├── Main.java                # Classe principal que lê JSON, processa operações e imprime impostos
├── domain                   # Pacote contendo entidades e DTOs
│   ├── dto                  # Objetos de transferência de dados (DTOs)
│   │   ├── TaxResultDTO.java
│   │   └── utils
│   │       └── TaxResultDTOSerializer.java
│   └── operation            # Classes que representam operações financeiras
│       ├── BuyOperation.java
│       ├── Operation.java
│       ├── SellOperation.java
│       └── dto
│           └── OperationDTO.java
└── services                 # Serviços para cálculo de impostos e gerenciamento de portfólio
    ├── portfolio
    │   └── PortfolioService.java
    └── tax
        └── TaxService.java
```

### 📌 Explicação
- Main.java: Responsável por processar a entrada e saída.
- domain.operation: Contém as classes de operações de compra e venda.
- services.portfolio.PortfolioService: Gerencia as operações do portfólio, aplicando as regras fiscais.
- services.tax.TaxService: Calcula os impostos sobre os lucros obtidos.

### 🏗️ Design Patterns e Princípios Utilizados
SOLID
O projeto segue os princípios SOLID para garantir modularidade, manutenção e extensibilidade do código:

- S: PortfolioService segue o princípio de responsabilidade única, separando lógica de portfólio da lógica de imposto.
- O: Novos tipos de operações podem ser adicionados sem modificar as classes existentes.
- L: BuyOperation e SellOperation estendem Operation, garantindo substituibilidade.
- I: A separação de DTOs evita que classes tenham dependências desnecessárias.
- D: Dependências como TaxService são injetadas, facilitando testes unitários.

### 📜 Dependências no pom.xml
O projeto utiliza Maven para gerenciamento de dependências. Principais bibliotecas:

- Jackson (jackson-databind): Para serialização/deserialização JSON.
- JUnit & Mockito: Para testes unitários e mock de serviços.

```xml
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.13.0</version>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>RELEASE</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.0.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

```

Maven foi escolhido pela sua facilidade de gerenciamento de dependências e integração com ferramentas de CI/CD.


### 🧪 Testes
- O projeto inclui testes unitários e de integração:

Para rodar os testes:

```shell
mvn test
```

Os testes validam cálculos de impostos, manipulação de portfólio e integração da Main.java com JSONs de entrada.

### 📌 Considerações Finais
Este projeto implementa o cálculo de impostos sobre ações de forma modular e extensível, respeitando os princípios SOLID. Com Maven, JUnit e Mockito, garantimos a qualidade do código e a facilidade de manutenção.