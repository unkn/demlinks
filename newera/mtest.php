<?php

require_once("mutex.php");

        $mutex = new Mutex();
        $mutex->init(1, "mutex_file.txt");
        $mutex->acquire();

sleep(2);
echo "a";
sleep(2);
        //Whatever you want single-threaded here...
        $mutex->release();
echo "done";

?>
