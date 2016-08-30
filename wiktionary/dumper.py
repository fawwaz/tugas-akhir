from lxml.cssselect import CSSSelector
from lxml.html import fromstring
import urllib2

url = 'http://en.wiktionary.org/wiki/Index:English/'
letters = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z']
for l in letters:
    req = urllib2.Request(url+l, headers={'User-Agent' : "Magic Browser"}) 
    con = urllib2.urlopen( req )
    response = con.read()
    h = fromstring(response)
    sel = CSSSelector("ol li a")

    for x in sel(h):
        print x.text.encode('utf-8')