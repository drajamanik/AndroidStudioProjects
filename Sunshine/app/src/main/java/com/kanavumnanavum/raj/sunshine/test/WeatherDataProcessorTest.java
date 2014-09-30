package com.kanavumnanavum.raj.sunshine.test;

import com.kanavumnanavum.raj.sunshine.app.WeatherDataProcessor;

/**
 * Created by raj on 9/19/14.
 */
public class WeatherDataProcessorTest
{
    public static void main(String args[]) throws Exception
    {
        WeatherDataProcessor wdp = new WeatherDataProcessor();
        String strArr[] = wdp.getWeatherDataFromJson(getStrJason(), 7);
        for(String s:strArr)
        {
            System.out.println(s);
        }



    }

    public static String getStrJason()
    {
        return
                "{\n" +
                        "   \"cod\":\"200\",\n" +
                        "   \"message\":0.1304,\n" +
                        "   \"city\":{\n" +
                        "      \"id\":\"5375480\",\n" +
                        "      \"name\":\"Mountain View\",\n" +
                        "      \"coord\":{\n" +
                        "         \"lon\":-122.075,\n" +
                        "         \"lat\":37.4103\n" +
                        "      },\n" +
                        "      \"country\":\"United States of America\",\n" +
                        "      \"population\":0\n" +
                        "   },\n" +
                        "   \"cnt\":7,\n" +
                        "   \"list\":[\n" +
                        "      {\n" +
                        "         \"dt\":1411156800,\n" +
                        "         \"temp\":{\n" +
                        "            \"day\":20.28,\n" +
                        "            \"min\":15.43,\n" +
                        "            \"max\":20.28,\n" +
                        "            \"night\":15.86,\n" +
                        "            \"eve\":17.55,\n" +
                        "            \"morn\":20.28\n" +
                        "         },\n" +
                        "         \"pressure\":1012.96,\n" +
                        "         \"humidity\":85,\n" +
                        "         \"weather\":[\n" +
                        "            {\n" +
                        "               \"id\":801,\n" +
                        "               \"main\":\"Clouds\",\n" +
                        "               \"description\":\"few clouds\",\n" +
                        "               \"icon\":\"02n\"\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"speed\":2.02,\n" +
                        "         \"deg\":275,\n" +
                        "         \"clouds\":12\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"dt\":1411243200,\n" +
                        "         \"temp\":{\n" +
                        "            \"day\":19.67,\n" +
                        "            \"min\":15.19,\n" +
                        "            \"max\":19.67,\n" +
                        "            \"night\":15.79,\n" +
                        "            \"eve\":16.89,\n" +
                        "            \"morn\":15.89\n" +
                        "         },\n" +
                        "         \"pressure\":1015.89,\n" +
                        "         \"humidity\":85,\n" +
                        "         \"weather\":[\n" +
                        "            {\n" +
                        "               \"id\":800,\n" +
                        "               \"main\":\"Clear\",\n" +
                        "               \"description\":\"sky is clear\",\n" +
                        "               \"icon\":\"01d\"\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"speed\":1.71,\n" +
                        "         \"deg\":225,\n" +
                        "         \"clouds\":0\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"dt\":1411329600,\n" +
                        "         \"temp\":{\n" +
                        "            \"day\":18.96,\n" +
                        "            \"min\":14.03,\n" +
                        "            \"max\":18.98,\n" +
                        "            \"night\":14.03,\n" +
                        "            \"eve\":16.6,\n" +
                        "            \"morn\":16.04\n" +
                        "         },\n" +
                        "         \"pressure\":1018,\n" +
                        "         \"humidity\":88,\n" +
                        "         \"weather\":[\n" +
                        "            {\n" +
                        "               \"id\":800,\n" +
                        "               \"main\":\"Clear\",\n" +
                        "               \"description\":\"sky is clear\",\n" +
                        "               \"icon\":\"01d\"\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"speed\":1.52,\n" +
                        "         \"deg\":266,\n" +
                        "         \"clouds\":0\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"dt\":1411416000,\n" +
                        "         \"temp\":{\n" +
                        "            \"day\":17.51,\n" +
                        "            \"min\":17.32,\n" +
                        "            \"max\":17.57,\n" +
                        "            \"night\":17.33,\n" +
                        "            \"eve\":17.57,\n" +
                        "            \"morn\":17.32\n" +
                        "         },\n" +
                        "         \"pressure\":1030.32,\n" +
                        "         \"humidity\":0,\n" +
                        "         \"weather\":[\n" +
                        "            {\n" +
                        "               \"id\":500,\n" +
                        "               \"main\":\"Rain\",\n" +
                        "               \"description\":\"light rain\",\n" +
                        "               \"icon\":\"10d\"\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"speed\":5.97,\n" +
                        "         \"deg\":330,\n" +
                        "         \"clouds\":35,\n" +
                        "         \"rain\":0.53\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"dt\":1411502400,\n" +
                        "         \"temp\":{\n" +
                        "            \"day\":17.64,\n" +
                        "            \"min\":17.59,\n" +
                        "            \"max\":17.84,\n" +
                        "            \"night\":17.84,\n" +
                        "            \"eve\":17.59,\n" +
                        "            \"morn\":17.61\n" +
                        "         },\n" +
                        "         \"pressure\":1028.58,\n" +
                        "         \"humidity\":0,\n" +
                        "         \"weather\":[\n" +
                        "            {\n" +
                        "               \"id\":500,\n" +
                        "               \"main\":\"Rain\",\n" +
                        "               \"description\":\"light rain\",\n" +
                        "               \"icon\":\"10d\"\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"speed\":2.3,\n" +
                        "         \"deg\":300,\n" +
                        "         \"clouds\":82,\n" +
                        "         \"rain\":0.83\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"dt\":1411585200,\n" +
                        "         \"temp\":{\n" +
                        "            \"day\":16.73,\n" +
                        "            \"min\":16.73,\n" +
                        "            \"max\":18.04,\n" +
                        "            \"night\":18.04,\n" +
                        "            \"eve\":17.74,\n" +
                        "            \"morn\":17.7\n" +
                        "         },\n" +
                        "         \"pressure\":1029.84,\n" +
                        "         \"humidity\":0,\n" +
                        "         \"weather\":[\n" +
                        "            {\n" +
                        "               \"id\":500,\n" +
                        "               \"main\":\"Rain\",\n" +
                        "               \"description\":\"light rain\",\n" +
                        "               \"icon\":\"10d\"\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"speed\":12.81,\n" +
                        "         \"deg\":337,\n" +
                        "         \"clouds\":0,\n" +
                        "         \"rain\":1.16\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"dt\":1411671600,\n" +
                        "         \"temp\":{\n" +
                        "            \"day\":17.78,\n" +
                        "            \"min\":17.78,\n" +
                        "            \"max\":18.67,\n" +
                        "            \"night\":18.53,\n" +
                        "            \"eve\":18.67,\n" +
                        "            \"morn\":17.97\n" +
                        "         },\n" +
                        "         \"pressure\":1027.72,\n" +
                        "         \"humidity\":0,\n" +
                        "         \"weather\":[\n" +
                        "            {\n" +
                        "               \"id\":800,\n" +
                        "               \"main\":\"Clear\",\n" +
                        "               \"description\":\"sky is clear\",\n" +
                        "               \"icon\":\"01d\"\n" +
                        "            }\n" +
                        "         ],\n" +
                        "         \"speed\":12.78,\n" +
                        "         \"deg\":348,\n" +
                        "         \"clouds\":3\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}";
    }
}
