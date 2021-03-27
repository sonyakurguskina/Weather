import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVReader;

public class File {

    public static void main(String[] args) throws IOException {
        //Создаем экземпляр класса измерений погоды
        Measures measures = new Measures();
        //считываем csv файл
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("dataexport_20210320T064822.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        }

        //помещаем цсв файл в лист, чтобы удобно было работать с элементами
        List<WeatherDay> days = new ArrayList<WeatherDay>();

        //помещаем значения каждого дня в отдельный лист дня
        //делаем трай кэтч чтобы убрать неинформативные строки без числовых значений
          for (int i=0; i< records.size(); i++){
            try {
                float f = Float.parseFloat(records.get(i).get(1));
                WeatherDay currentDay = new WeatherDay();
                currentDay.day = records.get(i).get(0);
                currentDay.measuring.add(Float.parseFloat(records.get(i).get(1)));
                currentDay.measuring.add(Float.parseFloat(records.get(i).get(2)));
                currentDay.measuring.add(Float.parseFloat(records.get(i).get(3)));
                currentDay.measuring.add(Float.parseFloat(records.get(i).get(4)));
                days.add(currentDay);
            } catch (Exception e) {
                System.out.println("Катч");
            } finally {
                continue;
            }
        }

        //делаем вычисления средних, максимальных, минимальных значений значений
        for (int j=3; j<days.size();j++){
            WeatherDay day = days.get(j);
            float temperature = day.measuring.get(0);
            float humidity = day.measuring.get(1);
            float windSpeed = day.measuring.get(2);

            measures.averageTemperature += temperature;
            measures.averageHumidity += humidity;
            measures.averageWindSpeed += windSpeed;
            measures.windDirection += day.measuring.get(3);

            if (measures.minHumidity > humidity){
                measures.minHumidity = humidity;
                measures.minHumidityDate = day.day;
            }
            if (measures.maxSpeedWind < windSpeed){
                measures.maxSpeedWind = windSpeed;
                measures.maxSpeedWindDate = day.day;
            }
            if (measures.maxTemperature < temperature) {
                measures.maxTemperature = temperature;
                measures.maxTemperatureDate = day.day;
            }
        }

        //вычисляем средние значения
        measures.averageTemperature /= days.size();
        measures.averageHumidity /= days.size();
        measures.averageWindSpeed /= days.size();
        measures.windDirection /= days.size();

        //записываем в файл наши измерения
        try(FileWriter writer = new FileWriter("answer.txt", false))
        {
            // запись всей строки
            String text = "Замеры погоды!\n";
            writer.write(text);
            // запись по символам
            writer.append("Средняя температура: " + measures.averageTemperature +'\n');
            writer.append("Средняя влажность: " + measures.averageHumidity +'\n');
            writer.append("Средняя скорость ветра: " + measures.averageWindSpeed +'\n');

            String direction = "";
            if (measures.windDirection >= 315 & measures.windDirection <= 360 || measures.windDirection >= 0 & measures.windDirection <= 45) {
                direction = "Север";
            }
            else if (measures.windDirection > 45 & measures.windDirection <= 135) {
                direction = "Восток";
            }
            else if (measures.windDirection > 135 & measures.windDirection <= 225) {
                direction = "Юг";
            }
            else if (measures.windDirection > 225 & measures.windDirection < 315) {
                direction = "Запад";
            }

            writer.append("Наиболее частое направление ветра: " + direction +'\n');

            String temp = measures.maxTemperatureDate;
            String str = temp.substring(0,4) + "-" + temp.substring(4,6) + "-" + temp.substring(6,8) + " " + temp.substring(9,11) + ":" + temp.substring(11,13);
            writer.append("Максимальная температура в: " + str +'\n');

            String temp1 = measures.minHumidityDate;
            String str1 = temp1.substring(0,4) + "-" + temp1.substring(4,6) + "-" + temp1.substring(6,8) + " " + temp1.substring(9,11) + ":" + temp1.substring(11,13);
            writer.append("Минимальная влажность в: " + str1 +'\n');

            String temp2 = measures.maxSpeedWindDate;
            String str2 = temp2.substring(0,4) + "-" + temp2.substring(4,6) + "-" + temp2.substring(6,8) + " " + temp2.substring(9,11) + ":" + temp2.substring(11,13);
            writer.append("Максимальная скорость ветра в: " + str2 +'\n');

            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
