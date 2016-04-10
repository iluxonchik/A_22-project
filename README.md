# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 022 - Campus Alameda

Jorge Heleno       número: 79042 email: jorge.heleno@tecnico.ulisboa.pt

Nuno Silva         número: 78454 email: nuno.m.ribeiro.silva@tecnico.ulisboa.pt

Illya Gerasymchuk  número: 78134 email: illya.gerasymchuk@tecnico.ulisboa.pt


Repositório:
[tecnico-distsys/A_22-project](https://github.com/tecnico-distsys/A_22-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

**Linux**


[1] Iniciar servidores de apoio

- JUDDI:

  Nos laboratórios da  [RNL](https://rnl.tecnico.ulisboa.pt/), correr
  ```
  $ juddi-startup
  ```
  para iniciar o jUDDI. A interface de controlo fica disponível em [http://localhost:9090/](http://localhost:9090/).

[2] Criar pasta temporária

```
cd ...
mkdir ...
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone ... 
```
*(colocar aqui comandos git para obter a versão entregue a partir da tag e depois apagar esta linha)*


[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

```
cd ...
mvn clean install
```


-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**

```
cd ...-ws
mvn clean install
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd ...-ws-cli
mvn clean install
```

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd ...-ws
mvn clean install
mvn exec:java
```


[2] Construir **cliente** e executar testes

```
cd ...-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
