from nltk.tokenize import TweetTokenizer
import codecs

tokenizer = TweetTokenizer()
texts = []
to_write = []
for i in range(1,11):
	f = codecs.open('dumped_text/DumpRetrievedTweet_%d'%(i),encoding='utf-8')
	for line in f:
		texts.append(line)


for t in texts:
	tokenizeds = tokenizer.tokenize(t)
	for tokenized in tokenizeds:
		to_write.append(tokenized + "\n")
	to_write.append('\n')

f2 = codecs.open('hasiltokenisasinltk',encoding='utf-8',mode='w+')
for tw in to_write:
	f2.write(tw)

f2.seek(0)
print repr(f2.readline()[:1])
f2.close()