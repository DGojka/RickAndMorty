Oto krótkie podsumowanie mojej pracy;

Tak jak w opisie aplikacja wyświetla liste postaci z serialu, którą można filtrować za pomocą nazwy lub filtrów (ulubione postacie, żywe, martwe);
W przypadku jednoczesnego zaznaczenia filtrów "żywe" i "martwe", wyświetlane są postacie posiadające status "unknown";

Lista początkowo zawiera tylko jedną strone postaci z api, aby zapobiec długiemu ładowaniu się listy;
Kiedy użytkownik przewija listę na sam dół, automatycznie doładują się kolejne postacie;

Dodatkowo zaimplemtementowałem feature, że jeśli użytkownik nie ma dostępu do internetu przy odpalaniu aplikacji, wyświetli się snackbar z napisem "No internet connection! Please try again.", a na środku ekranu widoczny jest circulating progress bar;
W produkcyjnej wersji aplikacji zrobiłbym customowy dialog, który pozwoliłby użytkownikowy ponowne załadowanie listy, jeśli połączył się z internetem. 
Wartobyłoby dodać też testy, których nigdy nie robiłem, ale jestem gotów się ich szybko i sprawnie nauczyć;
