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
cd
mkdir tmp_a_22
cd tmp_a_22
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone -b p1 https://github.com/tecnico-distsys/A_22-project.git
```


[4] Instalar módulos de bibliotecas auxiliares

- UDDINaming (usa o código fonte disponível em [uddi-naming.zip](http://disciplinas.tecnico.ulisboa.pt/leic-sod/2015-2016/labs/05-ws1/uddi-naming.zip))

  Obter código fonte, descomprimir, compilar e instalar:

  ```
  mkdir uddi-naming
  cd uddi-naming
  wget http://disciplinas.tecnico.ulisboa.pt/leic-sod/2015-2016/labs/05-ws1/uddi-naming.zip
  unzip uddi-naming.zip
  mvn clean install
  ```

- Compilar biblioteca com código partilhado do projeto:

  ```
  cd A_22-project/shared
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
