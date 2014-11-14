#
#Izbira atributov
#
train <- prepareDataSet(d,1)
learn <- train[1:800,]
test <- train[801:nrow(train),]

#drevo
wrapper(learn ,"WINNER",1)
wrapper(learn ,"WINNER",2)

#osnovno s tem da izpuscamo igre

	# Model bomo zgradili s pomocjo funkcije "CoreModel", ki potrebuje informacijo o tem, kateri tip modela naj zgradi.
	# Funkcijo za gradnjo modela napisemo tako, da klicu funkcije "CoreModel" dodamo parameter za izbiro tipa modela.
	mymodel.coremodel <- function(formula, data, target.model){CoreModel(formula, data, model=target.model)}

	# Funkcijo za generiranje napovedi napisemo tako, da iz dobljenih napovedi modela obdrzimo samo oznake razredov.
	# Ko model vrne zahtevane napovedi, ga ne potrebujemo vec - zato ga odstranimo iz pomnilnika.
	mypredict.coremodel <- function(object, newdata) {pred <- predict(object, newdata)$class; destroyModels(object); pred}

rezultati.izpuscanja = vector()
for (i in 1:50)
{
	train <- prepareDataSet(d,i)

	train=predelaj.razmerja(train)

	# 10-kratno precno preverjanje 
	res <- errorest(WINNER~., data=train, model = mymodel.coremodel, predict = mypredict.coremodel, target.model = "tree")
	
	uspesnost = 1-res$error
	
	rezultati.izpuscanja[i]=uspesnost
	print(i) 
}

plot(rezultati.izpuscanja, type="l")


#osnovno s tem da se spreminja k-najbljizjih sosedov
train <- prepareDataSet(d,1)
observed <- test$WINNER
rezultati.knn = vector()
for (i in 1:500)
{
	cm.knn <- CoreModel(WINNER ~ ., data = learn, model="knn", kInNN = i)
	predicted <- predict(cm.knn, test, type="class")

	rezultat = CA(observed, predicted)

	rezultati.knn[i] = rezultat
	print(i) 

}
plot(rezultati.knn, type="l")
which(rezultati.knn ==max(rezultati.knn))
#Osnovno

# predelaj vhodne podatke v ucno mnozico
train <- prepareDataSet(d,1)
names(train)

# razdeljevanje v ucno/testno mnozico
izbira <- sample(1:nrow(train), size=100, replace=F)
learn <- train[-izbira,]
test <- train[izbira,]

#vecinski razred
majority.class <- names(which.max(table(learn$WINNER)))
majority.class
sum(test$WINNER== majority.class) / length(test$WINNER)

#ODLOCITVENO DREVO
library(rpart)
dt <- rpart(WINNER ~ ., data = learn)
plot(dt)
text(dt, pretty = 0)

#dobimo prave vrednosti testnih primerov
observed <- test$WINNER
#observed
# funkcija za klasificirat z drevesom
predicted <- predict(dt, test, type = "class")
#predicted
# Zgradimo tabelo napacnih klasifikacij
t <- table(observed, predicted)
#t

#testiranje modela
predicted <- predict(dt, test, type = "class")
CA(observed, predicted)
Sensitivity(observed, predicted, "A")
Specificity(observed, predicted, "A")

#brier
predMat <- predict(dt, test, type = "prob")
#predMat
obsMat <- model.matrix(~WINNER-1, test)
#obsMat
brier.score(obsMat, predMat)

predMat <- predict(learn, test, type = "prob")
obsMat <- model.matrix(~WINNER-1, test)
brier.score(obsMat, predMat)

# NAIVNI BAYESOV KLASIFIKATOR

nb <- naiveBayes(WINNER  ~ ., data = learn)

#testiranje modela
observed <- test$WINNER
predicted <- predict(nb, test, type="class")

CA(observed, predicted)
Sensitivity(observed, predicted, "A")
Specificity(observed, predicted, "A")


# K-NAJBLIZJIH SOSEDOV
cm.knn <- CoreModel(WINNER ~ ., data = learn, model="knn", kInNN = 1)
predicted <- predict(cm.knn, test, type="class")

CA(observed, predicted)
Sensitivity(observed, predicted, "A")
Specificity(observed, predicted, "A")



# NAKLJUCNI GOZD
rf <- randomForest(WINNER ~ ., data = learn)

predicted <- predict(rf, test, type="class")
CA(observed, predicted)
Sensitivity(observed, predicted, "A")
Specificity(observed, predicted, "A")


predMat <- predict(rf, test, type = "prob")
brier.score(obsMat, predMat)

mypredict.generic <- function(object, newdata){predict(object, newdata, type = "class")}
mypredict.rf <- function(object, newdata){predict(object, newdata, type = "class")}
errorest(WINNER~., data=learn, model = randomForest, predict = mypredict.generic)

# SVM

sm <- svm(WINNER ~ ., data = learn)
predicted <- predict(sm, test, type="class")
CA(observed, predicted)
Sensitivity(observed, predicted, "A")
Specificity(observed, predicted, "A")


# UMETNE NEVRONSKE MREZE

norm.data <- scale.data(rbind(learn,test))
norm.learn <- norm.data[1:nrow(learn),]
norm.test <- norm.data[-(1:nrow(learn)),]
nn <- nnet(WINNER ~ ., data = norm.learn, size = 5, decay = 0.0001, maxit = 10000)
predicted <- predict(nn, norm.test, type = "class")
CA(observed, predicted)
Sensitivity(observed, predicted, "A")
Specificity(observed, predicted, "A")


#z razlicnimi atributi
#nn <- nnet(WINNER ~ ., data = norm.learn, size = i, decay = 0.0001 * (10*i), maxit = i*1000)
train <- prepareDataSet(d,1)
learn <- train[1:800,]
test <- train[801:nrow(train),]


norm.data <- scale.data(rbind(learn,test))
norm.learn <- norm.data[1:nrow(learn),]
norm.test <- norm.data[-(1:nrow(learn)),]

rezultati.knn = vector()
for (i in 1:10)
{
	
	nn <- nnet(WINNER ~ ., data = norm.learn, size = i, decay = 0.00001 *( 10*i) , maxit = 1000*i)

	predicted <- predict(nn, norm.test, type = "class")
	CA(observed, predicted)

	rezultat = CA(observed, predicted)

	rezultati.knn[i] = rezultat
	print(i) 

}
plot(rezultati.knn, type="l")
which(rezultati.knn ==max(rezultati.knn))
max(rezultati.knn)
#
#Izracun modelov
#
train <- prepareDataSet(d,1)
learn <- train[1:800,]
test <- train[801:nrow(train),]

poracunaj()

# primerjava osnovnega z tem da predelamo atribute tako da so razmerja
test = predelaj.razmerja(test)
learn = predelaj.razmerja(learn)

summary(test)

test=zracunaj.zamenjaj.skoki(test)

wrapper(learn,"WINNER")

print("drevo")
dt <- rpart(WINNER ~ HOME_FTA + AWAY_TO + HOME_TO + AWAY_DRB + AWAY_2PA + AWAY_ORB, data = learn)
observed <- test$WINNER
predicted <- predict(dt, test, type = "class")
print(CA(observed, predicted))
print(Sensitivity(observed, predicted, "A"))
print(Specificity(observed, predicted, "A"))


