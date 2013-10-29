<?php
$file = $_GET["provider"] . '.csv';
// Open the file to get existing content
$current = file_get_contents($file);
// Append a new person to the file
$current .= $_GET["lat"] . "; " . $_GET["lon"] . "; " . $_GET["accuracy"] . "; " . $_GET["time"] . "\n";
// Write the contents back to the file
file_put_contents($file, $current);
?>
loaded
