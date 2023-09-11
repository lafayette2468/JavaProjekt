# Vizsgaremek

## Leírás

A választott témaköröm egy vállalatirányítási rendszer három entitással.
Mivel a végzettségem és a foglalkozásom is a pénzügyi területhez tartozik, emiatt ez a témakör az, amiben leginkább a való élethez köthető példát tudtam elképzelni.
A programban lehetséges vevőket felvinni, egy vevőhöz több megrendelést létrehozni, megvalósulás esetén kiszámlázni, meghiúsulás esetén helyzettől függően törölni vagy sztornózni.
A végpontok kialakítása során igyekeztem olyan kéréseket megvalósítani, amelyek valós helyzetben is felmerülhetnek.

## Felépítés

### Customer

A `Customer` entitás a következő attribútumokkal rendelkezik:

* `id`
* `name` - vevő neve, szöveg, kötelező
* `taxNumber` - adószám, szöveg, kötelező
* `zipCode` - irányítószám, szöveg, kötelező
* `city` - város, szöveg, kötelező
* `address` - cím, szöveg, kötelező
* `orders` - order entitás listája

Végpontok: 

| HTTP metódus | Végpont                 | Leírás                                                                 |
| ------------ | ----------------------- | ---------------------------------------------------------------------- |
| GET          | `"/api/customers"`      | lekérdezi az összes entitást                                           |
| GET          | `"/api/customers/{id}"` | lekérdez egy entitást `id` alapján                                     |
| POST         | `"/api/customers"`      | rögzít egy új entitást                                                 |
| PUT          | `"/api/customers/{id}"` | módosít egy entitást `id` alapján                                      |



---

### Order 

Az `Order ` entitás a következő attribútumokkal rendelkezik:

* `id`
* `orderNumber` - megrendelés száma, egyedi, kötelező
* `orderDate` - megrendelés dátuma
* `itemDescription` - leírás, szöveg, kötelező
* `netUnitPrice` - nettó egységár, egész szám
* `quantity` - mennyiség, egész, pozitív szám
* `customer` - kapcsolódó vevő entitás
* `invoice` - kapcsolódó számla entitás

A `Customer` és az `Order` entitások között kétirányú, 1-n kapcsolat van.

Végpontok:

| HTTP metódus | Végpont                      | Leírás                                                               |
| ------------ | -----------------------      | -------------------------------------------------------------------- |
| GET          | `"/api/orders"`              | lekérdezi az invoice kapcsolattal nem rendelkező entitásokat         |
| POST         | `"/api/orders/{customerId}"` | rögzít egy új entitást egy adott vevőhöz                             |
| DELETE       | `"/api/orders/{id}"`         | töröl egy entitást `id` alapján                                      |
| PUT          | `"/api/orders/{id}"`         | módosít egy entitást `id` alapján                                    |

A törlés és a módosítás csak abban az esetben engedélyezett, amennyiben még nem tartozik számla a megrendeléshez.

---

### Invoice 

Az `Invoice ` entitás a következő attribútumokkal rendelkezik:

* `id`
* `invoiceNumber` - számla száma, egyedi, kötelező
* `invoiceDate` - számla kelte, dátum, kötelező
* `fulfilmentDate` - teljesítés dátuma, dátum, kötelező
* `dueDate` - fizetési határidő, dátum, kötelező, nem lehet a múltban
* `vat` - ÁFA, enum
* `methodOfPayment` - Fizetés módja, enum
* `order` - kapcsolódó megrendelés entitás

Az `Order` és az `Invoice` entitások között kétirányú, 1-1 kapcsolat van.

Végpontok:

| HTTP metódus | Végpont                          | Leírás                                                               |
| ------------ | ---------------------------------| -------------------------------------------------------------------- |
| GET          | `"/api/invoices"`                | lekérdezi az összes entitást                                         |
| POST         | `"/api/invoices/{orderId}"`      | rögzít egy új entitást egy adott megrendeléshez                      |
| POST         | `"/api/orders/credit-note/{id}"` | rögzít egy stornó megrendelést és számlát `id` alapján               |


A rögzítés csak akkor engedélyezett, ha még nincs ahhoz a megrendeléshez számla. 
A stornó számlaszáma az eredeti számla száma `C/` előtaggal és a megrendelésben negatív összeggel, így csak akkor rögzíthető, ha még nem létezik az eredeti számlához stornó.

---


## Technológiai részletek

Háromrétegű alkalmazás, a repository fájlok felelősek az adatbázissal történő kapcsolattartásért. A Service réteg a repository-ból kapott adatokat dolgozza fel az üzleti logika mentén, Mapstruct használatával DTO-kat továbbít. A Controller réteg pedig a felhasználóval tartja a kapcsolatot.
Az adatbázist a MariaDB kezeli, a kérések kipróbálásához a SwaggerUI került be a függőségek közé. Az integrációs tesztekhez a WebTestClientet használtam.
Az adatbázis Docker segítségével fut, illetve a program tartalmaz egy Dockerfile-t az image létrehozásához.

---