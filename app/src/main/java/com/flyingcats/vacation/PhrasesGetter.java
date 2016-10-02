package com.flyingcats.vacation;


import android.util.Log;

public class PhrasesGetter {



    public String getPhrases(String language, String kind){
        Log.d("CS591", language + " " + kind);
        return TranslationActivity.preferenceSettings.getString(language + " " + kind, "error1");
    }

    public void initializePhrases() {

        String whereTerminal = "Excuse me, where is Terminal ___?";
        String whereBaggage = "Excuse me, where is the baggage claim?";
        String costCoffee = "How much does a cup of coffee cost?";
        String whereRestroom = "Excuse me, where is the restroom?";
        String goodRestaurant = "Where is a good restaurant around here?";
        String whereLandmark = "Excuse me, where is [place, landmark]?";
        String whereUSEmbassy = "Excuse me, where is the US embassy?";
        String whereHotel = "Where is the closest hotel near me?";

        String SpanishWhereTerminal = "Disculpe, ¿dónde está la terminal ___?";
        String SpanishWhereBaggage = "Disculpe, ¿dónde está el reclamo de equipaje?";
        String SpanishCostCoffee = "¿Cuánto cuesta una taza de café costo?";
        String SpanishWhereRestroom = "Disculpe, ¿dónde está el baño?";
        String SpanishGoodRestaurant = "¿Dónde hay un buen restaurante por aquí?";
        String SpanishWhereLandmark = "Disculpe, ¿dónde está [___]?";
        String SpanishWhereUSEmbassy = "Disculpe, ¿dónde está la embajada de Estados Unidos?";
        String SpanishWhereHotel = "¿Dónde está el hotel más cercano?";

        String FrenchWhereTerminal = "Excusez-moi, où est la borne ___?";
        String FrenchWhereBaggage = "Excusez-moi, où est la salle des bagages?";
        String FrenchCostCoffee = "Combien coûte une tasse de café?";
        String FrenchWhereRestroom = "Excusez-moi, où sont le toilette?";
        String FrenchGoodRestaurant = "Où est un bon restaurant dans les environs?";
        String FrenchWhereLandmark = "Excusez-moi, où est ___?";
        String FrenchWhereUSEmbassy = "Excusez-moi, où c\'est l\'ambassade des états-unis?";
        String FrenchWhereHotel = "Où est l\'hôtel le plus proche?";

        String GermanWhereTerminal = "Entschuldigen Sie, wo ist die Klemme ___?";
        String GermanWhereBaggage = "Entschuldigen sie bitte, wo ist die gepäckausgabe?";
        String GermanCostCoffee = "Wie viel kostet eine Tasse Kaffee?";
        String GermanWhereRestroom = "Entschuldigen Sie, wo ist die Toilette?";
        String GermanGoodRestaurant = "Wo ist ein gutes restaurant hier in der Gegend?";
        String GermanWhereLandmark = "Entschuldigen Sie, wo ist ___?";
        String GermanWhereUSEmbassy = "Entschuldigen Sie, wo ist die US-Botschaft?";
        String GermanWhereHotel = "Wo ist das nächste hotel?";

        String RussianWhereTerminal = "Простите, где находится терминал Б3? -  Prostite, gde nahodetsia terminal ___?";
        String RussianWhereBaggage = "Простите, где багаж заберать? - Prostite, gde bagazh zaberat";
        String RussianCostCoffee = "Сколько чашка кофе стоит? - Skolko chashka coffie stoyiet?";
        String RussianWhereRestroom = "Извините, где туалет? Izvenite, gde tualet?";
        String RussianGoodRestaurant = "Где есть хороший ресторан радом с здеcь?";
        String RussianWhereLandmark = "Простите, где [___]? Prostite, gde ";
        String RussianWhereUSEmbassy = "Простите, где посольство США? Prostite, gde posolstva es, esh, e, a";
        String RussianWhereHotel = " Где находится ближайшая гостиница? Gde nahoditsa blizhaeshaya gostinitza";

        String JapaneseWhereTerminal = "(日本語) - すみません、ターミナルはどこですか。Sumimasen, Taminaru B wa doko desuka?";
        String JapaneseWhereBaggage = "(日本語)  - すみません、手荷物受取所はどこですか。Sumimasen, te nimotsu uketori-jyo wa doko desu ka?";
        String JapaneseCostCoffee = "(日本語)  - コーヒーはいくらですか。Kōhī wa ikuradesu ka?";
        String JapaneseWhereRestroom = "(日本語)  - すみません、トイレはどこですか。Sumimasen, toire wa doko desu ka?";
        String JapaneseGoodRestaurant = "(日本語) - この辺にいいレストランがありますか。どこですか。Kono hen ni ī resutoran ga arimasu ka. Dokodesu ka.";
        String JapaneseWhereLandmark = "(日本語) - すみません、X はどこですか。Sumimasen, X wa dokodesu ka?";
        String JapaneseWhereUSEmbassy = "(日本語) - すみません、米国大使館はどこですか。Sumimasen, Beikoku taishikan wa dokodesu ka?";
        String JapaneseWhereHotel = "(日本語) - すみません、一番近くて600ドル以下のホテルはどこですか。Sumimasen, ichiban chikakute roppyaku-doru ika no hoteru wa dokodesu ka?";


        TranslationActivity.preferenceEditor.putString("Spanish Airport/Train Station", whereTerminal +"\n" + SpanishWhereTerminal + "\n" + "\n" +  whereHotel + "\n" + SpanishWhereHotel + "\n" + "\n" + whereBaggage + "\n" + SpanishWhereBaggage + "\n" + "\n" + whereRestroom + "\n" + SpanishWhereRestroom + "\n" + "\n" + whereUSEmbassy +  "\n" + SpanishWhereUSEmbassy );
        TranslationActivity.preferenceEditor.putString("Spanish Coffee Shop", costCoffee + "\n" + SpanishCostCoffee  +"\n" + "\n" + whereRestroom + "\n" + SpanishWhereRestroom);
        TranslationActivity.preferenceEditor.putString("Spanish Restaurant", whereRestroom + "\n" + SpanishWhereRestroom + "\n" + "\n" + whereHotel + "\n" + SpanishWhereHotel);
        TranslationActivity.preferenceEditor.putString("Spanish On the street", whereLandmark + "\n" + SpanishWhereLandmark + "\n" + "\n" + whereHotel + "\n" + SpanishWhereHotel + "\n" + "\n" + goodRestaurant + "\n" + SpanishGoodRestaurant);
        TranslationActivity.preferenceEditor.putString("Spanish Hotel", whereLandmark + "\n" + SpanishWhereLandmark + "\n" + "\n" +whereRestroom + "\n" +SpanishWhereRestroom + "\n" + "\n" + whereUSEmbassy + "\n" + SpanishWhereUSEmbassy);

        TranslationActivity.preferenceEditor.putString("French Airport/Train Station", whereTerminal +"\n" + FrenchWhereTerminal + "\n" + "\n" + whereHotel + "\n" + FrenchWhereHotel + "\n" + "\n" + whereBaggage + "\n" + FrenchWhereBaggage + "\n" + "\n" + whereRestroom + "\n" + FrenchWhereRestroom + "\n" + "\n" + whereUSEmbassy +  "\n" + FrenchWhereUSEmbassy );
        TranslationActivity.preferenceEditor.putString("French Coffee Shop", costCoffee + "\n" + FrenchCostCoffee  +"\n" + "\n" + whereRestroom + "\n" + FrenchWhereRestroom);
        TranslationActivity.preferenceEditor.putString("French Restaurant", whereRestroom + "\n" + FrenchWhereRestroom + "\n" + "\n" + whereHotel + "\n" + FrenchWhereHotel);
        TranslationActivity.preferenceEditor.putString("French On the street", whereLandmark + "\n" + FrenchWhereLandmark + "\n" + "\n" + whereHotel + "\n" + FrenchWhereHotel + "\n" + "\n" + goodRestaurant + "\n" + FrenchGoodRestaurant);
        TranslationActivity.preferenceEditor.putString("French Hotel", whereLandmark + "\n" + FrenchWhereLandmark + "\n" + "\n" +whereRestroom + "\n" +FrenchWhereRestroom + "\n" + "\n" + whereUSEmbassy + "\n" + FrenchWhereUSEmbassy);

        TranslationActivity.preferenceEditor.putString("German Airport/Train Station", whereTerminal +"\n" + GermanWhereTerminal + "\n" + "\n" + whereHotel + "\n" + GermanWhereHotel + "\n" + "\n" + whereBaggage + "\n" + GermanWhereBaggage + "\n" + "\n" + whereRestroom + "\n" + GermanWhereRestroom + "\n" + "\n" + whereUSEmbassy +  "\n" + GermanWhereUSEmbassy );
        TranslationActivity.preferenceEditor.putString("German Coffee Shop", costCoffee + "\n" + GermanCostCoffee  +"\n" + "\n" + whereRestroom + "\n" + GermanWhereRestroom);
        TranslationActivity.preferenceEditor.putString("German Restaurant", whereRestroom + "\n" + GermanWhereRestroom + "\n" + "\n" + whereHotel + "\n" + GermanWhereHotel);
        TranslationActivity.preferenceEditor.putString("German On the street", whereLandmark + "\n" + GermanWhereLandmark + "\n" + "\n" + whereHotel + "\n" + GermanWhereHotel + "\n" + "\n" + goodRestaurant + "\n" + GermanGoodRestaurant);
        TranslationActivity.preferenceEditor.putString("German Hotel", whereLandmark + "\n" + GermanWhereLandmark + "\n" + "\n" +whereRestroom + "\n" +GermanWhereRestroom + "\n" + "\n" + whereUSEmbassy + "\n" + GermanWhereUSEmbassy);

        TranslationActivity.preferenceEditor.putString("Russian Airport/Train Station", whereTerminal +"\n" + RussianWhereTerminal + "\n" + "\n" + whereHotel + "\n" + RussianWhereHotel + "\n" + "\n" + whereBaggage + "\n" + RussianWhereBaggage + "\n" + "\n" + whereRestroom + "\n" + RussianWhereRestroom + "\n" + "\n" + whereUSEmbassy +  "\n" + RussianWhereUSEmbassy );
        TranslationActivity.preferenceEditor.putString("Russian Coffee Shop", costCoffee + "\n" + RussianCostCoffee  +"\n" + "\n" + whereRestroom + "\n" + RussianWhereRestroom);
        TranslationActivity.preferenceEditor.putString("Russian Restaurant", whereRestroom + "\n" + RussianWhereRestroom + "\n" + "\n" + whereHotel + "\n" + RussianWhereHotel);
        TranslationActivity.preferenceEditor.putString("Russian On the street", whereLandmark + "\n" + RussianWhereLandmark + "\n" + "\n" + whereHotel + "\n" + RussianWhereHotel + "\n" + "\n" + goodRestaurant + "\n" + RussianGoodRestaurant);
        TranslationActivity.preferenceEditor.putString("Russian Hotel", whereLandmark + "\n" + RussianWhereLandmark + "\n" + "\n" +whereRestroom + "\n" +RussianWhereRestroom + "\n" + "\n" + whereUSEmbassy + "\n" + RussianWhereUSEmbassy);

        TranslationActivity.preferenceEditor.putString("Japanese Airport/Train Station", whereTerminal +"\n" + JapaneseWhereTerminal + "\n" + "\n" + whereHotel + "\n" + JapaneseWhereHotel + "\n" + "\n" + whereBaggage + "\n" + JapaneseWhereBaggage + "\n" + "\n" + whereRestroom + "\n" + JapaneseWhereRestroom + "\n" + "\n" + whereUSEmbassy +  "\n" + JapaneseWhereUSEmbassy );
        TranslationActivity.preferenceEditor.putString("Japanese Coffee Shop", costCoffee + "\n" + JapaneseCostCoffee  +"\n" + "\n" + whereRestroom + "\n" + JapaneseWhereRestroom);
        TranslationActivity.preferenceEditor.putString("Japanese Restaurant", whereRestroom + "\n" + JapaneseWhereRestroom + "\n" + "\n" + whereHotel + "\n" + JapaneseWhereHotel);
        TranslationActivity.preferenceEditor.putString("Japanese On the street", whereLandmark + "\n" + JapaneseWhereLandmark + "\n" + "\n" + whereHotel + "\n" + JapaneseWhereHotel + "\n" + "\n" + goodRestaurant + "\n" + JapaneseGoodRestaurant);
        TranslationActivity.preferenceEditor.putString("Japanese Hotel", whereLandmark + "\n" + JapaneseWhereLandmark + "\n" + "\n" +whereRestroom + "\n" +JapaneseWhereRestroom + "\n" + "\n" + whereUSEmbassy + "\n" + JapaneseWhereUSEmbassy);


        TranslationActivity.preferenceEditor.apply();
    }

}
