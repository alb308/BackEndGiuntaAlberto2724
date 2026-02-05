# BetFlow Manager

Sistema gestionale per il Matched Betting sviluppato con Spring Boot 3 e PostgreSQL.

## Panoramica del Progetto

BetFlow Manager e' un'applicazione backend che permette di gestire operazioni di matched betting, tracciando identita', conti gioco, promozioni e operazioni finanziarie (depositi, prelievi, scommesse).

### Funzionalita' Principali

- **Gestione Utenti**: Registrazione con upload avatar, autenticazione JWT, 3 ruoli (ADMIN, MANAGER, OBSERVER)
- **Gestione Identita'**: CRUD completo per i clienti gestiti con tracking documenti
- **Piattaforme**: Gestione bookmaker e exchange
- **Conti Gioco**: Tracciamento saldi e stato account per ogni piattaforma
- **Promozioni**: Monitoraggio bonus e rollover
- **Operazioni Finanziarie**: Depositi, prelievi e scommesse con ereditarieta' JPA
- **Statistiche**: Calcolo profitti per identita' e dashboard aggregata
- **Notifiche**: Alert Telegram per scadenze documenti e promozioni

## Stack Tecnologico

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Spring GraphQL** (Punto Extra)
- **PostgreSQL**
- **Lombok**
- **Cloudinary** (upload immagini)
- **Telegram Bot API** (notifiche automatiche schedulate)
- **ExchangeRate-API** (conversione valuta - Punto Extra)

## Modello Dati (9 Tabelle)

```
User (1) -----> (*) Identity (1) -----> (*) Account (1) -----> (*) Promotion
                                              |
                                              v
                                    FinancialOperation (abstract)
                                         /    |    \
                                   Deposit  Withdrawal  BetOperation

Platform (1) -----> (*) Account
```

### Ereditarieta' JPA (JOINED)
- `FinancialOperation` (classe astratta padre - tabella `financial_operations`)
  - `Deposit` (tabella `deposits` - metodo pagamento)
  - `Withdrawal` (tabella `withdrawals` - stato e data arrivo)
  - `BetOperation` (tabella `bet_operations` - evento, quota, esito)

La strategia JOINED crea tabelle separate per ogni sottoclasse, con foreign key verso la tabella padre.

## Requisiti di Sistema

- Java 17+
- PostgreSQL 14+
- Maven 3.8+

## Variabili d'Ambiente

Crea un file `application.properties` in `src/main/resources/` con le seguenti configurazioni:

```properties
# Database PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/betflow_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

# JWT
jwt.secret=YOUR_JWT_SECRET_KEY_MIN_256_BITS
jwt.expiration=86400000

# Cloudinary
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET

# Telegram Bot
telegram.bot.token=YOUR_BOT_TOKEN
telegram.bot.chat-id=YOUR_CHAT_ID
```

## Istruzioni di Avvio

### 1. Clona il repository
```bash
git clone https://github.com/alb308/BackEndGiuntaAlberto2724.git
cd BackEndGiuntaAlberto2724
```

### 2. Configura le variabili d'ambiente (Opzionale)
Il file `docker-compose.yml` è già configurato per funzionare out-of-the-box con variabili di default.
Se vuoi personalizzare le chiavi (JWT, Cloudinary, Telegram), modifica `src/main/resources/application.properties` prima di avviare il container.

### 3. Avvia l'applicazione (Docker - Consigliato)
Esegui il comando dalla root del progetto:
```bash
docker-compose up -d --build
```
Questo avvierà sia il database PostgreSQL che l'applicazione backend.
L'app sarà disponibile su `http://localhost:8080`.

### Metodo alternativo (Maven locale)
Se preferisci eseguire l'app senza Docker (assicurati di avere PostgreSQL attivo su porta 5432):
```bash
mvn spring-boot:run
```

## Testing Automatizzato

Il progetto include test unitari e di integrazione (JUnit 5 + Mockito + H2).

### Esecuzione Test
```bash
mvn test
```
Questo comando eseguira' la suite di test verificando:
- Logica di business (UserService)
- Endpoint di autenticazione e sicurezza (AuthController)

## API Endpoints

### Autenticazione
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| POST | `/api/auth/register` | Registrazione utente | Pubblico |
| POST | `/api/auth/login` | Login | Pubblico |
| GET | `/api/auth/me` | Profilo utente corrente | Autenticato |
| PATCH | `/api/auth/me/avatar` | Aggiorna avatar | Autenticato |

### Utenti (ADMIN)
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/users` | Lista utenti | ADMIN |
| GET | `/api/users/{id}` | Dettaglio utente | ADMIN |
| PUT | `/api/users/{id}` | Modifica utente | ADMIN |
| DELETE | `/api/users/{id}` | Elimina utente | ADMIN |

### Identita'
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/identities` | Lista identita' | Tutti |
| GET | `/api/identities/{id}` | Dettaglio | Tutti |
| POST | `/api/identities` | Crea identita' | ADMIN, MANAGER |
| PUT | `/api/identities/{id}` | Modifica | ADMIN, MANAGER |
| DELETE | `/api/identities/{id}` | Elimina | ADMIN, MANAGER |

### Piattaforme
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/platforms` | Lista piattaforme | Tutti |
| POST | `/api/platforms` | Crea piattaforma | ADMIN, MANAGER |
| DELETE | `/api/platforms/{id}` | Elimina | ADMIN |

### Account
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/accounts` | Lista account | Tutti |
| GET | `/api/accounts?identityId={id}` | Filtra per identita' | Tutti |
| GET | `/api/accounts?platformId={id}` | Filtra per piattaforma | Tutti |
| POST | `/api/accounts` | Crea account | ADMIN, MANAGER |
| PUT | `/api/accounts/{id}` | Modifica | ADMIN, MANAGER |

### Promozioni
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/promotions` | Lista promozioni | Tutti |
| GET | `/api/promotions?status=ACTIVE` | Filtra per stato | Tutti |
| GET | `/api/promotions?expiringDays=7` | Promozioni in scadenza | Tutti |
| POST | `/api/promotions` | Crea promozione | ADMIN, MANAGER |
| PATCH | `/api/promotions/{id}/rollover` | Aggiorna rollover | ADMIN, MANAGER |

### Operazioni Finanziarie
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/operations/deposits` | Lista depositi | Tutti |
| POST | `/api/operations/deposits` | Crea deposito | ADMIN, MANAGER |
| GET | `/api/operations/withdrawals` | Lista prelievi | Tutti |
| POST | `/api/operations/withdrawals` | Crea prelievo | ADMIN, MANAGER |
| GET | `/api/operations/bets` | Lista scommesse | Tutti |
| POST | `/api/operations/bets` | Crea scommessa | ADMIN, MANAGER |

### Statistiche
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/statistics/dashboard` | Stats aggregate | Tutti |
| GET | `/api/statistics/profits` | Lista profitti tutte le identita' | Tutti |
| GET | `/api/statistics/profits/{identityId}` | Profitto identita' | Tutti |
| GET | `/api/statistics/profits/profitable` | Identita' in profitto | Tutti |
| GET | `/api/statistics/profits/unprofitable` | Identita' in perdita | Tutti |

### Conversione Valuta (ExchangeRate-API)
| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/currency/rates` | Tassi di cambio (base EUR) | Tutti |
| GET | `/api/currency/rates/{base}` | Tassi di cambio custom | Tutti |
| GET | `/api/currency/convert?amount=X&from=EUR&to=USD` | Conversione valuta | Tutti |
| GET | `/api/currency/convert/eur-to/{currency}?amount=X` | Da EUR a valuta | Tutti |
| GET | `/api/currency/convert/to-eur/{currency}?amount=X` | Da valuta a EUR | Tutti |

## Ruoli e Permessi

| Ruolo | Permessi |
|-------|----------|
| **ADMIN** | Accesso completo, CRUD su tutto |
| **MANAGER** | CRUD su Identity, Account, Promotion, Operations (no DELETE operations) |
| **OBSERVER** | Solo lettura su tutti i dati |

## Query Implementate

- **Filtraggio**: Account per identita'/piattaforma, promozioni per stato
- **Ordinamento**: Operazioni per data
- **Aggregazioni**: Somma depositi, prelievi, calcolo profitto
- **Condizioni multiple**: Promozioni attive in scadenza entro X giorni

## API Esterne Integrate

1. **Cloudinary**: Upload e gestione immagini profilo utente
2. **Telegram Bot API**: Notifiche automatiche schedulate per scadenze documenti e promozioni
3. **ExchangeRate-API** (Punto Extra): Conversione valuta in tempo reale con supporto per 150+ valute

## Notifiche Schedulate (Telegram)

Il sistema invia automaticamente notifiche via Telegram:

| Task | Frequenza | Descrizione |
|------|-----------|-------------|
| Daily Summary | Ogni giorno alle 9:00 | Riepilogo documenti e promozioni in scadenza |
| Document Expiry Check | Ogni giorno alle 8:00 | Alert per documenti che scadono entro 7 giorni |
| Promotion Expiry Check | Ogni 6 ore | Alert per promozioni che scadono entro 3 giorni |
| Expired Promotions Update | Ogni giorno a mezzanotte | Aggiorna automaticamente lo stato delle promozioni scadute |

## Collezione Postman

Importa il file `BetFlow_Manager.postman_collection.json` presente nella root del progetto.

## Autore

Alberto Giunta - Progetto Finale Backend

