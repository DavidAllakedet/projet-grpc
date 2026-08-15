# Guide Pratique — GraphQL vs gRPC avec Spring Boot

## Prérequis

| Outil | Version | Installation |
|-------|---------|-------------|
| Java | 21+ | `brew install openjdk@21` |
| Maven | 3.8+ | `brew install maven` |
| protoc | 3.25+ | `brew install protobuf` |
| grpcurl | 1.9+ | `brew install grpcurl` |
| Postman | 10+ | [postman.com/downloads](https://www.postman.com/downloads/) |

Vérifier les installations :
```bash
java -version
mvn -version
protoc --version
grpcurl --version
```

---

## Projet 1 — GraphQL (port 8080)

### 1. Démarrer le serveur

```bash
cd graphql-spring-boot && mvn spring-boot:run
```

### 2. Ouvrir GraphiQL (interface de test)

- Ouvrir le navigateur (Chrome/Safari)
- Taper : **http://localhost:8080/graphiql**
- L'interface GraphiQL s'ouvre avec un panneau de gauche (requête) et un panneau de droite (réponse)

### 3. Tester une requête

1. Dans le panneau **gauche**, coller une requête (voir ci-dessous)
2. Cliquer le bouton **Play** ▶ (ou `Cmd+Enter`)
3. La réponse s'affiche dans le panneau **droite**

### 4. Les requêtes à tester

**Lister tous les produits :**
```graphql
query {
  products {
    id
    name
    price
    category
    inStock
  }
}
```

**Chercher un produit par ID :**
```graphql
query {
  product(id: 1) {
    id
    name
    description
    price
  }
}
```

**Filtrer par catégorie :**
```graphql
query {
  productsByCategory(category: "Périphériques") {
    id
    name
    price
  }
}
```

**Créer un produit :**
```graphql
mutation {
  createProduct(input: {
    name: "Moniteur 4K"
    description: "27 pouces 4K UHD"
    price: 399.99
    category: "Informatique"
    inStock: true
  }) {
    id
    name
    price
  }
}
```

**Modifier un produit :**
```graphql
mutation {
  updateProduct(id: 1, input: {
    name: "Ordinateur Portable Pro"
    description: "Laptop 15 pouces, 32GB RAM"
    price: 1099.99
    category: "Informatique"
    inStock: true
  }) {
    id
    name
    price
  }
}
```

**Supprimer un produit :**
```graphql
mutation {
  deleteProduct(id: 7)
}
```

### Tester avec curl (terminal)

```bash
# Lister les produits
curl -s http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query":"{ products { id name price } }"}'

# Créer un produit
curl -s http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query":"mutation { createProduct(input: { name: \"Test\", price: 9.99, category: \"Test\", inStock: true }) { id name } }"}'
```

---

## Projet 2 — gRPC (port 9090)

### 1. Démarrer le serveur

```bash
cd grpc-spring-boot && mvn spring-boot:run
```

Le serveur démarre sur le port **9090**.

### 2. Tester avec Postman (recommandé)

Voir la section **"Tester avec Postman"** ci-dessous.

### 3. Tester avec grpcurl (terminal)

Le serveur utilise TLS, donc ajouter `-insecure` à chaque commande.

**Lister les services :**
```bash
grpcurl -insecure localhost:9090 list
```

**Lister les méthodes :**
```bash
grpcurl -insecure localhost:9090 list product.ProductService
```

**GetProduct (chercher par ID) :**
```bash
grpcurl -insecure -d '{"id": 1}' localhost:9090 product.ProductService/GetProduct
```

**ListProducts (lister tous) :**
```bash
grpcurl -insecure localhost:9090 product.ProductService/ListProducts
```

**ListProductsByCategory (filtrer) :**
```bash
grpcurl -insecure -d '{"category": "Informatique"}' localhost:9090 product.ProductService/ListProducts
```

**CreateProduct (créer) :**
```bash
grpcurl -insecure -d '{"name": "Clavier", "description": "Sans fil", "price": 59.99, "category": "Périphériques", "in_stock": true}' localhost:9090 product.ProductService/CreateProduct
```

**DeleteProduct (supprimer) :**
```bash
grpcurl -insecure -d '{"id": 7}' localhost:9090 product.ProductService/DeleteProduct
```

**Décrire le schéma :**
```bash
grpcurl -insecure localhost:9090 describe product.ProductService
```

### Tester avec Postman (GUI)

---

#### 1. Lancer le serveur gRPC

```bash
cd grpc-spring-boot && mvn spring-boot:run
```

Le serveur démarre avec **TLS** sur le port **9090**.

---

#### 2. Créer une requête gRPC dans Postman

1. Ouvrir Postman (`Cmd+Space` → `Postman`)
2. Cliquer **+** (en haut à gauche) pour créer une nouvelle requête
3. Dans le dropdown à côté de **HTTP**, choisir **gRPC**
4. Dans le champ URL, taper : `grpcs://localhost:9090`
   - **Important** : utiliser `grpcs://` (avec le **s**) car le serveur est en TLS
5. Cliquer **Next** → Postman utilise la **reflection** pour charger les services
6. Sélectionner le service : **product.ProductService**
7. Sélectionner la méthode (ex: **GetProduct**)
8. Dans le panneau **Message**, coller le body JSON
9. Cliquer **Invoke**

---

#### 3. Les requêtes à tester

##### GetProduct (chercher par ID)

- Méthode : `GetProduct`
- Message :
```json
{
  "id": 1
}
```

##### ListProducts (lister tous)

- Méthode : `ListProducts`
- Message : `{}`

##### ListProductsByCategory (filtrer)

- Méthode : `ListProducts`
- Message :
```json
{
  "category": "Informatique"
}
```

##### CreateProduct (créer)

- Méthode : `CreateProduct`
- Message :
```json
{
  "name": "Ecran 4K",
  "description": "Moniteur 27 pouces",
  "price": 399.99,
  "category": "Informatique",
  "in_stock": true
}
```

##### UpdateProduct (modifier)

- Méthode : `UpdateProduct`
- Message :
```json
{
  "id": 1,
  "name": "Laptop Pro",
  "description": "32GB RAM",
  "price": 1299.99,
  "category": "Informatique",
  "in_stock": true
}
```

##### DeleteProduct (supprimer)

- Méthode : `DeleteProduct`
- Message :
```json
{
  "id": 7
}
```

---

#### 4. Erreurs courantes

| Erreur | Cause | Solution |
|--------|-------|----------|
| `Connection refused` | Serveur éteint | Relancer `mvn spring-boot:run` |
| `Malformed response` | Mauvais protocole | Vérifier que c'est bien **gRPC** (pas HTTP) et `grpcs://` dans l'URL |
| `No service found` | Port incorrect | Vérifier `grpcs://localhost:9090` |

---

## Les deux en même temps

Ouvrir deux terminaux :

```bash
# Terminal 1 — GraphQL
cd graphql-spring-boot && mvn spring-boot:run

# Terminal 2 — gRPC
cd grpc-spring-boot && mvn spring-boot:run
```

| Projet | Port | Interface |
|--------|------|-----------|
| GraphQL | 8080 | http://localhost:8080/graphiql |
| gRPC | 9090 | grpcurl ou Postman collection |

---

## Structure des projets

```
graphql-spring-boot/
├── pom.xml
└── src/main/
    ├── java/com/example/graphql/
    │   ├── GraphQLApplication.java        # Point d'entrée
    │   ├── model/Product.java             # Entité JPA
    │   ├── repository/ProductRepository.java
    │   ├── service/ProductService.java    # Logique métier
    │   ├── resolver/ProductResolver.java  # Endpoints GraphQL
    │   └── config/DataInitializer.java    # Données de démo
    └── resources/
        ├── application.yml
        └── graphql/schema.graphqls        # Schéma GraphQL

grpc-spring-boot/
├── pom.xml
├── postman_collection.json       # Collection Postman gRPC
├── postman_environment.json      # Environnement Postman
└── src/main/
    ├── proto/product.proto                # Définition du service
    ├── java/com/example/grpc/
    │   ├── GrpcApplication.java
    │   ├── model/Product.java
    │   ├── repository/ProductRepository.java
    │   ├── service/ProductService.java
    │   ├── server/ProductGrpcService.java # Implémentation gRPC
    │   └── config/
    │       ├── DataInitializer.java
    │       └── GrpcServerConfig.java      # Config serveur gRPC
    └── resources/
        └── application.yml
```
