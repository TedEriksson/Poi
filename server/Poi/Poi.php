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
		return json_encode($results);
	}

	public function insert($token, $select, $where) {

	}
}