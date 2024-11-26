Programowanie urządzeń mobilnych laboratorium L_1_ 

# Dokumentacja projetu: Aplikacja FocusZone

## Zespoł projetowy:
_Mateusz Bocak
Gabriela Bieniek_

## Opis projektu

FocusZone to aplikacja mobilna na Androida, której celem jest pomoc użytkownikom w zarządzaniu czasem spędzanym na urządzeniach mobilnych. Dzięki blokowaniu aplikacji i stron internetowych oraz ustawianiu limitów czasowych, FocusZone wspiera budowanie zdrowych nawyków i ograniczanie rozpraszaczy.


## Zakres projektu opis funkcjonalności:

- **Blokowanie aplikacji**:
    - Ustawianie dziennego limitu czasu lub limit dla pojedynczej sesji i ich ilości dla wybranych aplikacji.
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
- Panel logowania

![image](https://github.com/user-attachments/assets/a271c59d-e3f5-4c74-99b5-7300d4db32a4)

...

## Baza danych
TODO
###### Diagram ERD

###### Opis bazy danych

## Wykorzystane uprawnienia aplikacji do:

- Usage Stats (PACKAGE_USAGE_STATS)
- Accessibility Service
- System Alert Window (SYSTEM_ALERT_WINDOW)
- Internet (INTERNET) (opcjonalnie)
- Biometric Authentication (USE_BIOMETRIC)
- Write External Storage (WRITE_EXTERNAL_STORAGE) (opcjonalnie)
- Access Network State (ACCESS_NETWORK_STATE)
- Alert systemowy (SYSTEM_ALERT_WINDOW)

## Dane potrzebne do konfiguracji podczas pierwszego uruchomienia (jeśli wymagane)

TODO