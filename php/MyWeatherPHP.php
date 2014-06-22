<?php
		$location = $_GET["location"];
		$locationType = $_GET["locationType"];
		$unit = $_GET["unit"];
		$woeidList = array();
		$url = "";
	function createGeoPlanetURL(){
		global $location,$locationType,$unit,$woeidList,$url;
		$loc =$location;
		if($locationType=="city"){
			$url = "http://where.yahooapis.com/v1/places\$and(.q('".$loc."'),.type(7))".";"."start=0".";"."count=5?appid=gabeecTV34E0fTtPbWtJDojDN2o_9PfeZYhgaETDL.n6CRTKWgsfvabMtmqdKyBzrsy4HQ8C";
		}
		else{
			$url="http://where.yahooapis.com/v1/concordance/usps/".$loc."?appid=gabeecTV34E0fTtPbWtJDojDN2o_9PfeZYhgaETDL.n6CRTKWgsfvabMtmqdKyBzrsy4HQ8C";		
		}
		$url = URLencode($url);
		//echo $url;
	}
	
	function fetchWoeid(){
		global $location,$locationType,$unit,$woeidList,$url;
		$xml=@simplexml_load_file($url);
		if($locationType=="city"){
   			foreach($xml->place as $child)
   			{
   				array_push($woeidList,$child->woeid);
   			}
		}
		else{
   			array_push($woeidList,$xml->woeid);
		}
	}
	
	function fetchWeatherDetails(){
		global $location,$locationType,$unit,$woeidList,$url;
		$city = "N/A";
		$unitCaps = "N/A";
		$region = "N/A";
		$country = "N/A";
		$latitude = "N/A";
		$longitude = "N/A";
		$weatherUrl = "N/A";
		$text = "N/A";
		$temperature = "N/A";
		$title = "N/A";
		$img = "N/A";
		$link = "N/A";
		$arrlength=count($woeidList);
		$count = 0;
		$display = "";
		$unitCaps = "";
		if($arrlength > 1){
			$arrlength = 1;
		}
		for($x=0;$x<$arrlength;$x++)
  		{
  			$weatherUrl = "http://weather.yahooapis.com/forecastrss?w=" .$woeidList[$x]. "&u=" . $unit;
  			//echo $weatherUrl;
  			$channel=simplexml_load_file($weatherUrl);
  			$namespaces = $channel->getNamespaces(true);
  			$title = $channel->channel->title;
  			if(strpos($title,"Error") !== false){
				continue;
  			}
  			else{
  			$count = $count + 1;
  			$city = $channel->channel->children($namespaces["yweather"])->location->attributes()->city;
  			$unitCaps = $channel->channel->children($namespaces["yweather"])->units->attributes()->temperature;
  			$region = $channel->channel->children($namespaces["yweather"])->location->attributes()->region;
  			$country = $channel->channel->children($namespaces["yweather"])->location->attributes()->country;			
  			$latitude = $channel->channel->item->children($namespaces["geo"])->lat;
  			$longitude = $channel->channel->item->children($namespaces["geo"])->long;
  			$text = $channel->channel->item->children($namespaces["yweather"])->condition->attributes()->text;
  			$temperature = $channel->channel->item->children($namespaces["yweather"])->condition->attributes()->temp;
  			$description = $channel->channel->item->description;
			$keywords = preg_split("/\"/", $description);
			$img = $keywords[1];
			$link = $keywords[3];
			}
			if($city==""){
				$city = "N/A";
			}
			if($unitCaps==""){
				$unitCaps = "N/A";
			}
			if($region==""){
				$region = "N/A";
			}
			if($country==""){
				$country = "N/A";
			}
			if($latitude==""){
				$latitude = "N/A";
			}
			if($longitude==""){
				$longitude = "N/A";
			}
			if($weatherUrl==""){
				$weatherUrl = "N/A";
			}
			if($text==""){
				$text = "N/A";
			}
			if($temperature==""){
				$temperature = "N/A";
			}
			if($title==""){
				$title = "N/A";
			}
			if($img==""){
				$img = "N/A";
			}
			if($link==""){
				$link = "N/A";
			}
			$display = $display. "<weather>";
			$display = $display. "<feed><![CDATA[".$weatherUrl."]]></feed>";
			$display = $display. "<link><![CDATA[".$link."]]></link>";
			$display = $display. "<location city=\"".$city."\" region=\"".$region."\" country=\"".$country."\"/>";
			$display = $display. "<units temperature=\"".$unitCaps."\"/>";
			$display = $display. "<condition text=\"".$text."\" temp=\"".$temperature."\"/>";
			$display = $display. "<img><![CDATA[".$img."]]></img>";
			$forecast = $channel->channel->item->children($namespaces["yweather"]);
			foreach($forecast->forecast as $child)
   			{
   				$day = $child->attributes()->day;
   				$low = $child->attributes()->low;
   				$high = $child->attributes()->high;
   				$forecast_text = $child->attributes()->text;
   				$display = $display. "<forecast day=\"".$day."\" low=\"".$low."\" high=\"".$high."\" text=\"".$forecast_text."\"/>";
   			}
			$display = $display. "</weather>";
			$city = "N/A";
			$unitCaps = "N/A";
			$region = "N/A";
			$country = "N/A";
			$latitude = "N/A";
			$longitude = "N/A";
			$weatherUrl = "N/A";
			$text = "N/A";
			$temperature = "N/A";
			$title = "N/A";
			$img = "N/A";
			$link = "N/A";
  		}
  		if($locationType=="city"){
  			$locationType="City";
  		}
  		else{
  			$locationType="Zip Code";
  		}
  		if($arrlength==0 || $count==0){
  			$display = "<center><b>Zero results found!!</b></center>";
  		}
  		echo $display;

	}
		createGeoPlanetURL();
		fetchWoeid();
		fetchWeatherDetails();
?>