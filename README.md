Programowanie urządzeń mobilnych laboratorium L_1

# Dokumentacja projetu: Aplikacja FocusZone

## Zespoł projetowy:
_Mateusz Bocak
Gabriela Bieniek_

## Opis projektu

FocusZone to aplikacja mobilna na Androida, której celem jest pomoc użytkownikom w zarządzaniu czasem spędzanym na urządzeniach mobilnych. Dzięki blokowaniu aplikacji i stron internetowych oraz ustawianiu limitów czasowych, FocusZone wspiera budowanie zdrowych nawyków i ograniczanie rozpraszaczy.


## Zakres projektu opis funkcjonalności:

- **Blokowanie aplikacji**:
    - Ustawianie dziennego limitu czasu i ich ilości dla wybranych aplikacji.
    - Blokada aplikacji po przekroczeniu ustawionego limitu.

- **Blokowanie stron internetowych**:
    - Dodaj strony do listy blokowanych.
    - Uniwersalna blokada stron działająca we wszystkich przeglądarkach.

- **Personalizacja komunikatów blokady**:
    - Dostosuj pełnoekranowe wiadomości wyświetlane przed otwarciem aplikacji lub strony.

- **Logowanie**:
    - Bezpieczny dostęp do aplikacji dzięki PIN'u/biometrii (odcisk palca, rozpoznawanie twarzy).

- **Stastytyki użytkowania**:
    - Monitoruj czas spędzony na aplikacjach i stronach w formie dziennych raportów (funkcja opcjonalna).

- **Tymczasowe wyłączenie blokad**:
  - Tryb "Emergency" - wyłącza obecne limity i blokady. Zaprojektowany w taki sposób żeby skutecznie zniechęcać użytkownika przed go nadużywaniem

## Panele / zakładki aplikacji 

- Panel rejestracji
- Panel logowania
- Ekran główny
- Ustawienia
- Dodaj/Edytuj limit dla aplikacji
- Dodaj/Edytuj blokowaną stronę

TODO
zdjęcia i pozostałe

## Baza danych

Aplikacja nie posiada bazy danych w rozumieniu relacyjnym.

Wszystkie dane zapisywane są w `sharedPreferences` pod odpowiednimi kluczami. Nie wszysktie potrzebują pełnych operacji CRUD.

Do zarządzania nimi została stworzona klasa `PreferenceManager`.

## Wykorzystane uprawnienia aplikacji do:

- Usage Stats (PACKAGE_USAGE_STATS) - sprawdzanie ile użytkownik korzysta z danych aplikacji
- Accessibility Service/VPN service - blokowanie ruchu sieciowego do wybranych stron
- System Alert Window (SYSTEM_ALERT_WINDOW) - alert całoekranowy
- Biometric Authentication (USE_BIOMETRIC) (opcjonalne)
- Access Network State (ACCESS_NETWORK_STATE)

## Dane potrzebne do konfiguracji podczas pierwszego uruchomienia (jeśli wymagane)

TODO