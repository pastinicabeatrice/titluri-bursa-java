# Titluri bursiere

Analiza titlurilor bursiere din fișier și bază de date SQLite, cu un server TCP care servește mai mulți clienți.

## Funcționalități

1. titlurile cu valoarea tranzacționată minimă și maximă
2. titlurile sortate descrescător după volum
3. titlurile cu diferența Max-Min de peste 1%, sortate descrescător
4. comunicare client-server TCP/IP: serverul (`ServerTitluri`, cu un pool de fire) răspunde la cererile mai multor clienți care trimit simultan simbolul unui titlu

Prețurile și volumele se citesc din `date/PretVolum.txt`, iar denumirile titlurilor din baza SQLite `date/Titluri.db`.

## Rulare

Se deschide folderul în IntelliJ IDEA, se adaugă `lib/sqlite-jdbc-3.30.1.jar` ca bibliotecă și se rulează `Main`.

Din linia de comandă:

```
javac -cp lib/sqlite-jdbc-3.30.1.jar -d out src/*.java
java -cp "out;lib/sqlite-jdbc-3.30.1.jar" Main
```

Pe Linux sau macOS separatorul de classpath este `:` în loc de `;`.
