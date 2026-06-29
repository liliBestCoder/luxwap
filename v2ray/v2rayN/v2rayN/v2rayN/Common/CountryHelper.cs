using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace v2rayN.Common;


public class CountryHelper
{
    public class CountryItem
    {
        [JsonPropertyName("alpha-2")]
        public string Code { get; set; }
        public string Name { get; set; }
    }


    private static string counties_json = @"
        [
          {""name"": ""China"", ""alpha-2"": ""CN""},
          {""name"": ""Russian Federation"", ""alpha-2"": ""RU""},
          {""name"": ""Turkmenistan"", ""alpha-2"": ""TM""},
          {""name"": ""India"", ""alpha-2"": ""IN""},
          {""name"": ""Türkiye"", ""alpha-2"": ""TR""},
          {""name"": ""Viet Nam"", ""alpha-2"": ""VN""},
          {""name"": ""Iran, Islamic Republic of"", ""alpha-2"": ""IR""},
          {""name"": ""Saudi Arabia"", ""alpha-2"": ""SA""},
          {""name"": ""Myanmar"", ""alpha-2"": ""MM""},
          {""name"": ""Egypt"", ""alpha-2"": ""EG""},
          {""name"": ""Pakistan"", ""alpha-2"": ""PK""},
          {""name"": ""United Arab Emirates"", ""alpha-2"": ""AE""},
          {""name"": ""Cuba"", ""alpha-2"": ""CU""},
          {""name"": ""Uzbekistan"", ""alpha-2"": ""UZ""},
          {""name"": ""Bangladesh"", ""alpha-2"": ""BD""},
          {""name"": ""Korea, Democratic People's Republic of"", ""alpha-2"": ""KP""},
          {""name"": ""Eritrea"", ""alpha-2"": ""ER""}
        ]
        ";
    public static List<CountryItem> GetCountries()
    {

        var jsonCountries = JsonSerializer.Deserialize<List<CountryItem>>(counties_json,
        new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

        return jsonCountries;
    }
}

