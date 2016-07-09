rm -rf assets/adblock*
wget http://winhelp2002.mvps.org/hosts.txt -O winhelp
wget https://hosts-file.net/ad_servers.txt -O hosts-file
wget https://adaway.org/hosts.txt -O adaway
wget https://raw.githubusercontent.com/vokins/yhosts/master/hosts.txt -O cn
cat cn >> new
cat winhelp >> new
cat adaway >> new
cat hosts-file >> new
cat new | sed '/#/d' | sed '/@/d' | gawk '!a[$2]++' | sed '/^$/d' | sed '/localhost/d' | gawk '{printf("127.0.0.1 %s\n",$2)}' | sort > final
split -a 1 -l 5000 final adblock
rm -rf final new hosts-file adaway winhelp cn
mv adblock* assets/