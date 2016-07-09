rm -rf assets/adblock*
wget http://winhelp2002.mvps.org/hosts.txt -O winhelp
wget https://hosts-file.net/ad_servers.txt -O hosts-file
wget https://adaway.org/hosts.txt -O adaway
wget https://raw.githubusercontent.com/vokins/yhosts/master/hosts.txt -O cn
vi nian-host
cat cn | sed -e 's/\r//g' >> new
cat winhelp | sed -e 's/\r//g' >> new
cat adaway | sed -e 's/\r//g' >> new
cat hosts-file | sed -e 's/\r//g' >> new
cat nian-host | awk '{printf("127.0.0.1 %s\n",$1)}' | sed -e 's/\r//g' >> new
cat new | sed '/#/d' | sed '/@/d' | gawk '!a[$2]++' |sed '/localhost/d' | gawk '{printf("127.0.0.1 %s\n",$2)}' |sort > final
split -a 1 -l 5000 final adblock
rm -rf final new hosts-file adaway winhelp cn nian-host
mv adblock* assets/
