<?php
//thanks to    mr dot smaon at gmail dot com

/*LICENSE*GNU*GPL************************************************************{{{
*
*                             dmental links
*    Copyright (C) 2006 AtKaaZ, AtKaaZ at users.sourceforge.net
*
*  ========================================================================
*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*  ========================================================================
*
* Description: mutex controlling single-threaded code, thus if parallel processes 
* are about to execute the same s-t code then only one of them will actually be
* executing it...
*
***************************************************************************}}}*/


    class Mutex/*{{{*/
    {
        private $id;
        private $sem_id;
        private $is_acquired = false;
        private $is_windows = false;
        private $filename = '';
        private $filepointer;

        function __construct()/*{{{*/
        {
            if(substr(PHP_OS, 0, 3) == 'WIN')
                $this->is_windows = true;
            //echo $this->is_windows;
        }/*}}}*/

        public function init($id, $filename = '')/*{{{*/
        {
            $this->id = $id;

            if($this->is_windows)
            {
                if(empty($filename)){
                    print "no filename specified for mutex in ".__FILE__." at line ".__LINE__;
                    return false;
                }
                else
                    $this->filename = $filename;
            }
            else
            {
                if(!($this->sem_id = sem_get($this->id, 1))){
                    print "Error getting semaphore";
                    return false;
                }
            }

            return true;
        }/*}}}*/

        public function acquire()/*{{{*/
        {
            if($this->is_windows)
            {
                if(($this->filepointer = @fopen($this->filename, "w+")) == false)
                {
                    print "error opening mutex file<br>";
                    return false;
                }

                if(flock($this->filepointer, LOCK_EX) == false)
                {
                    print "error locking mutex file<br>";
                    return false;
                }
            }
            else
            {
                if (! sem_acquire($this->sem_id)){
                    print "error acquiring semaphore";
                    return false;
                }
            }

            $this->is_acquired = true;
            return true;
        }/*}}}*/

        public function release()/*{{{*/
        {
            if($this->is_acquired) {
                // Release semaphore
                if ($this->is_windows) {
                        return fclose($this->filepointer);
                } else {
                        return sem_release($this->sem_id);
                }
            }
        }/*}}}*/

        public function getId()/*{{{*/
        {
            return $this->sem_id;
        }/*}}}*/
    }/*}}}*/

/*   Example use:

   <?php

       $mutex = new Mutex();
       $mutex->init(1, "mutex_file.txt");
       $mutex->acquire();

       //Whatever you want single-threaded here...
       $mutex->release();

   ?>
*/

// vim: fdm=marker

?>
