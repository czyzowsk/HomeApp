# HomeApp

Projekt: Damian Czyżowski

## Wprowadzenie

Aplikacja HomeApp jest wielofunkcyjną platformą do komunikacji z moduem sterującym podstawowe parametry Twojego domu. 
Podstawową funkcjonalnością jest możliwość automatycznego łączenia się ze swoim stanowiskiem domowym. Konieczność ingerencji użytkownika jest tyklo podczas pierwszego połączenia.

### Podstawowe funkcje 

* Szybkie sterowanie poprzez ekran główny bramą garażową i wjazdową
* Panel w pasku notyfikacji z funkcją szybkiego dostępu do powyższych funkcji
* Ekran kontrolowania poszczegolnych kondygnacji i sprawdzania poziomu oświetlenia i temperatury

## Omówienie aplikacji

### MainActivity
Ekran główny w na którym mamy do wyboru podstawowe funkcje takie jak brama garażowa i wjazdowa, stan domu na poszczególnych kondygnacjach oraz panel ustawień


Widok ekranu głównego:
![N|Solid](http://i.imgur.com/5eyQR1g.png)

### TaskService 
Klasa odpowiedzialna za obsługe w tle wyszukiwania domu
Ta usługa zarządza również notyfikacjami.

![N|Solid](http://i.imgur.com/SZzpkG7.png)
![N|Solid](http://i.imgur.com/kqvpNkz.png)
![N|Solid](http://i.imgur.com/RkW2nlt.png)

### HomeActivity
Ekran kontrolowania poszczegolnych kondygnacji i sprawdzania poziomu oświetlenia i temperatury.

![N|Solid](http://i.imgur.com/qCuz3DD.png)
![N|Solid](http://i.imgur.com/QKQrnkd.png)

### FindDevicesActivity
Jest to Dialog Activity w którym możemy odnaleźć urządzenia znajdujace się w naszym otoczeniu urządzenia Bluetooth

![N|Solid](http://i.imgur.com/saib286.png)

### SettingsActivity
Aktywność w kórej możemy zmienić domyślne urzadzenie.

![N|Solid](http://i.imgur.com/t0I2QHI.png)

## API

### Odbierane dane

Odbierane dane muszą zawierać się w następującej formule danych:
```
aa(informacja_ogólna)aa(temperatura_pok1)aa(temperatura_pok2)aa(temperatura_pok3)
przykład:
aa00000000aa30aa20aa30
```
### Wysyłane dane przez aplikacje
Dane wysyłane przez aplikacje:
```
'b' - otwarcie/zamkniecie bramy wjazdowej
'g' - otwarcie/zamkniecie bramy garazowej
"t030" - ustawienie temperatury na parterze na 30 stopni
"s150" - ustawienie oświetlenia na pierwszym piętrze na 50 procent
"d30" - ustawienie poziomu nachylenia dachu na poddaszu na 30 procent.
```

## Podsumowanie

Projekt może współpracować z każdym modulem obsługująćym transmisje szeregową Bluetooth. 

Aplikacja jest projektem zaliczeniowym na zajęcia: "Języki projektowania obiektowego", oraz częścią projektu "Dom inteligentny" na zajęcia "Podstawy mechatroniki"

