<?php
	error_reporting(E_ALL);
	include 'Poi/Poi.php';
	include 'connectinfo.php';
	$request = array();
	$uri_parts = explode('?', $_SERVER['REQUEST_URI'], 2);
	$url = $uri_parts[0];
	
	$segs = explode("/", $url);
	$segs = array_values(array_diff($segs, ["","api"]));

	if ($json = file_get_contents("php://input")) {
		$api = new Api($segs,$json);
	} else {
		$api = new Api($segs);
	}

	if($segs[0] == "points") {
		$poi = new Poi(DATABASE_HOSTNAME,DATABASE_DATABASE,DATABASE_USER,DATABASE_PASSWORD);
		
		if (isset($segs[1])) {
			if($json = file_get_contents("php://input")) {
				echo $poi->update(json_decode($json,true));
			} else {
				echo $poi->get($segs[1]);
			}
		} else {
			if($json = file_get_contents("php://input")) {
				echo " Insert";
			} else {
				$whereParams = $_GET;
				echo "SELECT<br><br>";
				var_dump($whereParams);
			}
		}
	}

	/**
	* 
	*/
	class Api {

		private $segs;
		private $json = null;
		
		function __construct($segs, $json = null) {
			$this->segs = $segs;
			$this->json = $json;
		}

		function create() {
			switch ($json) {
				case null:
					# code...
					break;
				
				default:
					# code...
					break;
			}
		}
	}

	class Get extends Api {

		function runTask() {
			echo "do get";
		}
	}
?>