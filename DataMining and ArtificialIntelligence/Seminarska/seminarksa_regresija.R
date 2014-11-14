#
#rezanje regresijkega drevesa
#

rt.model <- rpart(WINNER ~ ., learn, cp = 0)
plot(rt.model);text(rt.model, pretty = 0)

# izpisemo ocenjene napake drevesa za razlicne vrednosti parametra cp
cpTable = printcp(rt.model)
min(cpTable[,"xerror"])

rt.model2 <- prune(rt.model, cp = 0.0176958)
plot(rt.model2);text(rt.model2, pretty = 0)

observed <- test$WINNER
predicted <- predict(rt.model, test)
mae(observed, predicted)
rmae(observed, predicted, mean(learn$WINNER))

observed <- test$WINNER
predicted <- predict(rt.model2, test)
mae(observed, predicted)
rmae(observed, predicted, mean(learn$WINNER))

#
# k-nn slika
#
train <- prepareDataSetReg(d,1)
learn <- train[1:800,]
test <- train[801:nrow(train),]
learn <- predelaj.razmerja(learn)
test <-  predelaj.razmerja(test)
observed <- test$WINNER
rezultati.knn = vector()
for (i in 1:500)
{
	knn.model <- kknn(WINNER~ ., learn, test, k =i)

	predicted <- fitted(knn.model)
	rezultat = rmae(observed, predicted, mean(learn$WINNER))

	rezultati.knn[i] = rezultat
	print(i) 

}
plot(rezultati.knn, type="l")
which(rezultati.knn ==min(rezultati.knn))


#
# nevronske mreze parametri
#
train <- prepareDataSetReg(d,1)
learn <- train[1:800,]
test <- train[801:nrow(train),]
learn <- predelaj.razmerja(learn)
test <-  predelaj.razmerja(test)
norm.data <- scale.data(rbind(learn,test))
norm.learn <- norm.data[1:nrow(learn),]
norm.test <- norm.data[-(1:nrow(learn)),]

observed <- test$WINNER

rezultati.knn = vector()
for (i in 1:50)
{
	nn.model <- nnet(WINNER ~ ., norm.learn, size = i, decay = 0.00001 *(10*i), maxit = 10000*i, linout = T)
	predicted <- predict(nn.model, norm.test)

	rezultat = rmae(observed, predicted, mean(learn$WINNER))

	rezultati.knn[i] = rezultat
	print(i) 

}
plot(rezultati.knn, type="l")
which(rezultati.knn ==min(rezultati.knn))

#
#Izbira attributov
#
train <- prepareDataSetReg(d,1)
learn <- train[1:700,]
learn <- predelaj.razmerja(learn)
wrapperReg(learn ,"WINNER")

#
# Izracun modelov
#

train <- prepareDataSetReg(d,1)

learn <- train[1:700,]
test <- train[701:nrow(train),]
poracunajReg()
learn <- predelaj.razmerja(learn)
test <-  predelaj.razmerja(test)
poracunajReg()


poracunajReg <- function()
{
majority.class <- names(which.max(table(train$WINNER)))
print("povprecna vrednost:")
print(mean(train$WINNER))

# linearni model
print("linearni model")
lm.model <- lm(WINNER ~ ., data = learn)
lm.model

observed <- test$WINNER
predicted <- predict(lm.model, test)
print(mae(observed, predicted))
print(rmae(observed, predicted, mean(learn$WINNER)))
print(rmse (observed, predicted, mean(learn$WINNER)))

 
predicted
# regresijsko drevo
print("regresijsko drevo")

rt.model <- rpart(WINNER  ~ ., learn, cp =0.0176958)

observed <- test$WINNER
predicted <- predict(rt.model, test)
print(mae(observed, predicted))
print(rmae(observed, predicted, mean(learn$WINNER)))
print(rmse (observed, predicted, mean(learn$WINNER)))

# nakljucni gozd
print("nakljucni gozd")

rf.model <- randomForest(WINNER ~ ., learn)

observed <- test$WINNER
predicted <- predict(rf.model, test)
print(mae(observed, predicted))
print(rmae(observed, predicted, mean(learn$WINNER)))
print(rmse (observed, predicted, mean(learn$WINNER)))

# svm
print("svm")

svm.model <- svm(WINNER ~ ., learn)

observed <- test$WINNER
predicted <- predict(svm.model, test)
print(mae(observed, predicted))
print(rmae(observed, predicted, mean(learn$WINNER )))
print(rmse (observed, predicted, mean(learn$WINNER)))

#k-nn
print("knn")

knn.model <- kknn(WINNER~ ., learn, test, k = 42)

observed <- test$WINNER
predicted <- fitted(knn.model)
print(mae(observed, predicted))
print(rmae(observed, predicted, mean(learn$WINNER)))
print(rmse (observed, predicted, mean(learn$WINNER)))

#nevronska mreža

print("nevronska mreza")

#normalizacija
norm.data <- scale.data(rbind(learn,test))
norm.learn <- norm.data[1:nrow(learn),]
norm.test <- norm.data[-(1:nrow(learn)),]

nn.model <- nnet(WINNER ~ ., norm.learn, size = 3, decay = 0.0001, maxit = 100000, linout = T)

observed <- test$WINNER
predicted <- predict(nn.model, norm.test)
print(mae(observed, predicted))
print(rmae(observed, predicted, mean(learn$WINNER)))
print(rmse (observed, predicted, mean(learn$WINNER)))
}










