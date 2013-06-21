#!/usr/bin/perl
#
#########################################################################################################################       
#                                                                                                                       #
#	@version $Revision$ ($Author$) $Date$     #
# 	Parallel loading of logical backups                                                                             #
#                                                                                                                       #          
#########################################################################################################################

use strict;
use warnings;
use threads;

my ($sourcedir,$dbname) = @ARGV;
my @filelist;
my $filepattern = 'vista_[a-z0-9_]+\.full.sql';
my $max_threads = 16;


sub load_data
{
        my $start_time = time();   
        my $fh = shift;
        
        system("psql -U vista -h localhost -d $dbname -f $fh -w  > /dev/null 2>&1");
        print "Loaded file $fh in ".(time() - $start_time)." seconds\n";
        
}

if (!defined($sourcedir) or $sourcedir eq "")
{
	$sourcedir = '/home/akinareevski/tmp/import/';
}
else
{       
        # Make sure there is a trailing slash in $logdir 
        $sourcedir =~ s/\/*$/\//;
}

if (!defined($dbname) or $dbname eq "")
{
	$dbname = 'vista';
	
}

# Rename _admin_,_expiring_ and public schemas

system("psql -U vista -h localhost -d $dbname -c 'ALTER SCHEMA _admin_ RENAME TO new_admin'");
system("psql -U vista -h localhost -d $dbname -c 'ALTER SCHEMA _expiring_ RENAME TO new_expiring'");
system("psql -U vista -h localhost -d $dbname -c 'ALTER SCHEMA public RENAME TO new_public'");


# Get all files in $sourcedir that match pattern

opendir(my $dh, $sourcedir) || die "can't opendir $sourcedir: $!";
    @filelist =  grep { /^$filepattern/ && -f "$sourcedir$_" } readdir($dh);
    
closedir $dh;


foreach my $file (@filelist)
{
        $file = $sourcedir.$file;     
        
        while (scalar(threads->list(threads::running)) >= $max_threads)
        {
                sleep(1);
        }
        
        my $thread = threads->create(\&load_data, $file);
}

while(threads->list(threads::running))
{
        sleep(1);
}

exit 0;
