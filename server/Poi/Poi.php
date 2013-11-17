<?
class Poi {

	private $pdo;

	const POINTS_TABLE = 'points';

	function __construct($hostname,$database,$username,$password) {
		$this->pdo = new PDO("mysql:host=$hostname;dbname=$database",$username,$password);
	}

	public function get($id = null) {
		if($id == null) {
			$statement = $this->pdo->prepare("SELECT * FROM points");
			$statement->execute();
		} else {
			$statement = $this->pdo->prepare("SELECT * FROM points WHERE id = :id");
			$statement->execute(array('id' => $id));
		}
		$results = $statement->fetchAll(PDO::FETCH_ASSOC);
		$results = array('points' => $results);
		return json_encode($results);
	}

	public function update($vals, $token = null) {
		$updateString = "UPDATE points SET ";
		$isFirst = true;
		foreach ($vals as $key => $value) {
			$pdoVals[":$key"] = $value;
			if ($key != 'id') {
				if($isFirst) {
					$updateString .= "$key=:$key";
					$isFirst = false;
				} else {
					$updateString .= ", $key=:$key";
				}
			}
		}
		$updateString .= " WHERE id=:id";
		$statement = $this->pdo->prepare($updateString);
		if($statement->execute($pdoVals)) {
			return true;
		}
	}

	public function insert($vals, $token = null) {
		
	}
}