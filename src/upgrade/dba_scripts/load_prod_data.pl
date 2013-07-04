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
use Getopt::Long;


my $sourcedir = '/home/akinareevski/tmp/import/';
my $dbname = 'vista';
my $pattern = 'vista_[a-z0-9_]+\.full.sql';
my $threads = 16;
my @filelist;

GetOptions(     "sourcedir:s" => \$sourcedir,
                "dbname:s" => \$dbname,
                "pattern:s" => \$pattern,
                "threads:i" => \$threads);

# Make sure there is a trailing slash in $sourcedir
$sourcedir =~ s/\/*$/\//;
                
                
#print "SOURCEDIR $sourcedir\n";
#print "DBNAME $dbname\n";
#print "PATTERN $pattern\n";
#print "THREADS $threads\n";

sub load_data
{
        my $start_time = time();   
        my $fh = shift;
        
        system("psql -U vista -h localhost -d $dbname -f $fh -w  > /dev/null 2>&1");
        print "Loaded file $fh in ".(time() - $start_time)." seconds\n";
        
}



# Rename _admin_,_expiring_ and public schemas

system("psql -U vista -h localhost -d $dbname -c 'ALTER SCHEMA _admin_ RENAME TO new_admin'");
system("psql -U vista -h localhost -d $dbname -c 'ALTER SCHEMA _expiring_ RENAME TO new_expiring'");
system("psql -U vista -h localhost -d $dbname -c 'ALTER SCHEMA public RENAME TO new_public'");


# Get all files in $sourcedir that match pattern

opendir(my $dh, $sourcedir) || die "can't open dir $sourcedir: $!";
    @filelist =  grep { /^$pattern/ && -f "$sourcedir$_" } readdir($dh);
    
closedir $dh;


foreach my $file (@filelist)
{
        $file = $sourcedir.$file; 
        
        while (scalar(threads->list(threads::running)) >= $threads)
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
