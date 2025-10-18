# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4pvFiqK4lABJEmApJvoYu40vuDJMlO+lzt5d5LsKYoSm6MpymW7xKpgKrBhqAByEDRTAABmvhNsODpJr6zpdhu7pbh5IHVP6brJWAIqZRwUYxnGhRabl8DIKmMDpgAjAROaqHm8zQUWJbdilbzpTVDb0dunlUre-K+WATRGep07lipMBqTsgW8sFlTLjANRIGl8obQCcWqhqi1bDsMDZDdaAgNAKLgDeQU5RZVkRcV+WlRJpYAOreA4l3GQCdUoLGCnofCyatdh7VOF1oxjD1fUFmMg3QPU4oPcWKDgOtS07BNTYlV521zYecgoM+8Tnpe17ZYuu3Co+Ab019nbtjppaCcJGSqABmDcz9NQvIRq3zCRqHfBRVH1lLtFQ5hsNgDheEEfpNFkWMsuIfLpHEwxnjeH4-heCg6AxHEiTm5bvO+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDLrSFKx+Zn+sH6BC+H8J5Z29Q2fYDv2RRkBIe531kwuo5GCg3DHpectoHTepp+gW3Z-ewoyHnTKGDTHrxeqMAY7Aifio4DsveTi7vS6hWvpnFn+nA7BoBAYA09ASAAF4oGDEPxk1MMlKr8OI9m-KowNxaYzAIBjxP1lCQ7htTVzMcvKZsIYc1LswCMmlldDlSiWr+GjHRTbG8x-gouu-hsDig1PxNEMAADiSoNBO2HqWBo4Cva+3sEqIOhc9aNWftpS+D9I5oGvl+Z2+UE5okgTmEulF0EZ05lnHyMBGST0vOQouFd9xV3qAdI6xo0EhybhqcezAKLTzngxRmAo75EIHhzSy7446i3gIfBheohHz2jODBqocV5tU6t1Le+Yd5DTujjJ6zATq7C-gxUmM1Xr0joUyUhagmGUO7pXEK9QwrrnsRoUmYcb5gWWMguYABJaQBYOrhGCIEGA+DzKwJeP4pUwTQnhMiU-X6ytV7vyzPEoJIT6hhIiVExsRsmKmw4AAdjcE4FATgYgRmCHALiAA2eAE5DD2JgEUFWhDfqu1aB0JBKDpjcPQFmAJKBEpKlSYmSo3MXi4LWGMiZcxom31kVZSm6J7GOKQgspUSyuTnxoSOeaNNtnl2caw1x+1KCHXlLgs6CUYD8K4UoqAs8UAiNmr3NZ-dPryEObE+RaB+FTzeXPBe6jl6vxVmmBGOjcx6MLLvUs2NHp4xMYTAEZ9LEyC+QeVpWzFlKhYTtIUK41wQNit4mZ2CRjZJQIkvJyTCnC3ET0h+9LGUwHySkjR0KMkwFwh-OlYwxlcp5YUyaP9TaWDzjZTYVskAJDALKvsEAFUACkIDikpXMGIyRQBqk6avbpcjmjMhkj0MZqD4LoKzNgBAwBZVQDgBAGyUBdk5Kmash4vi5nDLQKpR1zrXXus9Qy6QKzY4dmkQVAAVtqtAWz5nrWDZQUN0Bw37KobGwUoiTmMNwSSnKzN2E3M4fc3hTyj6CLBR8i5wU+4FT+cAAFsiR4KNBe8iFkMoUtQFdopGKNEXo2RVje6aL8amOxZnKxPdRz0MJXs4lDaS1kpgFqnV9iHnN2ebW95nzrFiKba6GcUjByAvsSKXsCAe1L0wc1N+8NMxDt0f1JFBixkwFrPWcxbbfVfivtHXxItdLepfv2tqQqsx-ulQELwTrFXKoQ-KRAwZYDAGwA6wgeQCgdJge2uB7tPbe19sYUONK-UPyjaa9Z3A8DGAxDmwcc7s71BAPRgEOgmOrqZuujIMwIA0EKraPFbCYACaE20qBvGxGlok0cKTbNbVIQ0FW0ewKa2sK0PIPQBhZMiyspQEs-774aZBfuaQAAhO9GC0mPphTAAArHhB+yM31o1buxhRxptM6DPqIwz-cex9n-bM6jwGCFsrkY-PlkG4bQc-kUzAQA](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4pvFiqK4lABJEmApJvoYu40vuDJMlO+lzt5d5LsKYoSm6MpymW7xKpgKrBhqAByEDRTAABmvhNsODpJr6zpdjAEYzLGlgRVuHkgdU-puslYAiplHBRjGcaFFpuXwMgqYwOmACMBE5qoebzNBRYlt2KVvOlDUNvRN5BTlFlWWV8jbp5VK3vyvlgE0RnqdO5YqTAak7IFvLBZUy4wGF67HQCcWqhqO1bDsMDZK9aAgNAKLgHNZ0LXlnb1E9xl3RV7WgfUADq3gOMD6lNSgsYKeh8LJp12HdU4fWjGMA1DQWYyjdA9Tip9xYoOAR27TsM1NmD63zfSh5yCgz7xOel7Xtli4XcKj4Bpz5X5e2OmloJwkZKoAGYKLlUSWBhEHfMJGod8FFUfWKu0SjmHo2AOF4QR+k0WRYzq4hmukbTDGeN4fj+F4KDoDEcSJI7zvi74WCiYKEONNIEb8RG7QRt0PRyaoCnDObSE6x+Zn+jH6Aywn8IA5ZLrWUJXv2RRkBIe5wteX99JGCg3DHpeGtoBzer5+gp0LgKIX1NI5dMoYbMevF6qFcW0BZ8Jjhe79TdyxnBVs-XrVFxZ-pwOwaAQGAbPQEgABeKAI0j8ZtWjJT65j2PZvy+Mjf3pYgIvy+D171uj-u49WSMmmz5UosvKZsIYeDVVga-8tdYHwNvhUYdEmy22Yv4FE65-DYHFBqfiaIYAAHElQaB9nPUsDQUHBzDvYJU0cq4WxnoA+O38wJJzQF-L8vt8r1GQDkNBOZa6URIYXTsgpuajhgIyFel5WHV0bo-FuMAahIDSvKKh90EowCXswCia9N4MW4U-TOyUu5C04b-eW9QF5oHkavKAG8t7RkRi1OO+8uq9X6qffM58xrvTJt9Zgt1djgJURtRci1M6aJWvTGQXieF8OYWoDEwjzpCnqNdVB6CZG93kcaS8SiUCeMZs3HxBVEq9gQKE1QSCcirXHv6ZKeSozmORnvSook0xY1sbmexhYL71GSrEnMV1poeIfsFTJLwABSZAWiJV4eg6yaIAETxFqnfpgzhkEPaYwsANDzJYJmUMkZCzxmWOqXrEBWYBnrPmWoMZOQumQPthwAA7G4JwKAnAxAjMEOAXEABs8AJyGFCTAIoes6G6MaK0Do+DCHTGIUhLMRzEpKgmT-d+0yYAjCoWsSF0KU4UL+RPeozN0ShMESQ5FSooVzA4ZM4uTctpszxQXbpOVeb1HEZIpJ8ESHxI1IkxRxjlE0u8enKyGjBb+Lfh2Goejr78L1Ck7eFiqkdWAUfepg1GmE2aU4r6FNXHUwBPfAJ3CxwfNxSi4l3Lm50v5qE41aiuwjGWEcgAktIAsPVwjBECDACZg5yFfheDapU9rHXOtdcsn+qyEU+rmH6+oTqXVuu2bKrquFQHWrGHah1kaA0xtmucgIlhy42U2C7JACQwA5r7BAfNfSIDijaYYfwyRQBqh+QfDFkkmjMhkj0I5RDmXgtGNgBAwAc1QDgBAGyUACXhukDCtODwKEvCRUdftg7h2jvHSge1Qbp0eXqAAK0rWgXF86+0DsoMu6Aq77UksHAzEuwSmSUqoRE2lUSxGUAkVIsFyce5spvhykxaSb2WpaRAPxwAik6JFfAMVRiTFSsqX-IB1i6k4zxkqompZSZqspm4++qjwNIhA2B695LeF+TZCBx9PMokxOWsAWU8oCO4d6Rud0WjSVEZ8iEpUGIU1rDdJyCjJrn3MmwFodE1aYCQ11PqU9UAeh8YrAUcT9rWVyJ-ckzlqSLV4czk0RdJ6R3QDAyG0JIocmwd3vB9qNTMaZmQ3Y4aTTHFHJgLWesXTGO8szi-IzcLZ0Io3cU-+sbrMJqzGcpi9svADoLUWqL8pEDBlgMAbAfbCB5EU428wzbsEByDiHMOxg46+a9f5tFtCmMgG4HgYw4TCOBPSViqrAIdC1dw3SmAGQZgQBoMx20QT7zCg60cbrny4ltefZ1kbAtu3oA0F+yDBif2P1E7ofQYgPPCqspQEsPnhXzyg-uaQAAhczpDExWIxgAVjwgi3G9mCZoaxWK40y2dA4f69pgqPY+y7Y-qV2W4HdJToQxjULYDGwMSAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
