library(rpart)
library(ipred)
library(e1071)
library(randomForest)
library(nnet)
library(CORElearn)
library(kknn)

#preberi vhodno datoteko
d <- read.table("nba0809.txt", header = T, sep = ",")

# Pomozna funkcija, ki izracuna povprecno vrednost statistik na podlagi podanih podatkov,
# imena ekipe, datuma ter logicne vrednosti, ali nas zanimajo domace ali gostujoce tekme
getAverageStats <- function(inputData, teamName, date, home)
{
	if (home)
		sel <- inputData[which(inputData$HOME_NAME == teamName & inputData$DATE < date), 13:21]
	else
		sel <- inputData[which(inputData$AWAY_NAME == teamName & inputData$DATE < date), 4:12]

	if (nrow(sel) > 0)
		apply(sel, 2, mean)
	else
		NULL
}
####################################################################
#
# Mere za ocenjevanje u?enja v regresiji
#
####################################################################

mae <- function(observed, predicted)
{
	mean(abs(observed - predicted))
}

rmae <- function(observed, predicted, mean.val) 
{  
	sum(abs(observed - predicted)) / sum(abs(observed - mean.val))
}

mse <- function(observed, predicted)
{
	mean((observed - predicted)^2)
}

rmse <- function(observed, predicted, mean.val) 
{  
	sum((observed - predicted)^2)/sum((observed - mean.val)^2)
}
# Funkcija prejme vhodne podatke in jih predela v ucno mnozico za klasifikacijo
prepareDataSet <- function(inputData, startIndex)
{
	# pripravimo prostor za nase podatke 
	# (stevilo stolpcev poznamo, stevila vrstic pa ne, zato nastavimo enako kot v izhodiscni mnozici) 
	outputData <- matrix(nrow = nrow(inputData), ncol=18)
	
	# vektor oznak zmagovalca posamezne tekme
	winner <- vector()
	
	# stevec tekem
	j <- 0

	# za vsako vrstico izhodiscne tabele
	for (i in startIndex:nrow(inputData))
	{
		# izracunaj povprecne statistike domacina (iz prejsnjih tekem)
		homeStats <- getAverageStats(inputData, inputData$HOME_NAME[i], inputData$DATE[i], TRUE)
		
		# ce se jih ne da izracunati, gremo na naslednjo tekmo
		if (is.null(homeStats))
			next

		# izracunaj povprecne statistike gosta (iz prejsnjih tekem)
		awayStats <- getAverageStats(inputData, inputData$AWAY_NAME[i], inputData$DATE[i], FALSE)

		# ce se jih ne da izracunati, gremo na naslednjo tekmo		
		if (is.null(awayStats))
			next
	
		# kdo je zmagovalec te tekme
		if (inputData$HOME_PTS_FINAL[i] > inputData$AWAY_PTS_FINAL[i])
			winner <- c(winner, "H")
		else
			winner <- c(winner, "A")

		# povecamo stevec tekem
		j <- j + 1

		# vstavimo novo vrstico s statistikami
		outputData[j,] <- c(awayStats, homeStats)
	}

	# obdrzimo samo polne vrstice (le-teh je j)
	outputData <- data.frame(outputData[1:j,])

	# dodamo stolpec z oznakami zmagovalcev
	outputData <- cbind(outputData, winner)

	# poimenujemo stolpce (imena statistik + zmagovalec)
	colnames(outputData) <- c(colnames(inputData)[4:21], "WINNER")
	
	# vrnemo rezultat
	outputData
}  
# Funkcija prejme vhodne podatke in jih predela v ucno mnozico za regresijo
prepareDataSetReg <- function(inputData, startIndex)
{
	# pripravimo prostor za nase podatke 
	# (stevilo stolpcev poznamo, stevila vrstic pa ne, zato nastavimo enako kot v izhodiscni mnozici) 
	outputData <- matrix(nrow = nrow(inputData), ncol=18)
	
	# vektor oznak zmagovalca posamezne tekme
	winner <- vector()
	
	# stevec tekem
	j <- 0

	# za vsako vrstico izhodiscne tabele
	for (i in startIndex:nrow(inputData))
	{
		# izracunaj povprecne statistike domacina (iz prejsnjih tekem)
		homeStats <- getAverageStats(inputData, inputData$HOME_NAME[i], inputData$DATE[i], TRUE)
		
		# ce se jih ne da izracunati, gremo na naslednjo tekmo
		if (is.null(homeStats))
			next

		# izracunaj povprecne statistike gosta (iz prejsnjih tekem)
		awayStats <- getAverageStats(inputData, inputData$AWAY_NAME[i], inputData$DATE[i], FALSE)

		# ce se jih ne da izracunati, gremo na naslednjo tekmo		
		if (is.null(awayStats))
			next
	
		# razlika tock tekme
		winner <- c(winner, inputData$HOME_PTS_FINAL[i] - inputData$AWAY_PTS_FINAL[i])

		# povecamo stevec tekem
		j <- j + 1

		# vstavimo novo vrstico s statistikami
		outputData[j,] <- c(awayStats, homeStats)
	}

	# obdrzimo samo polne vrstice (le-teh je j)
	outputData <- data.frame(outputData[1:j,])

	# dodamo stolpec z oznakami zmagovalcev
	outputData <- cbind(outputData, winner)

	# poimenujemo stolpce (imena statistik + zmagovalec)
	colnames(outputData) <- c(colnames(inputData)[4:21], "WINNER")
	
	# vrnemo rezultat
	outputData
}  
CA <- function(prave, napovedane)
{
	t <- table(prave, napovedane)

	sum(diag(t)) / sum(t)
}
Sensitivity <- function(prave, napovedane, poz.razred)
{
	t <- table(prave, napovedane)

	t[poz.razred, poz.razred] / sum(t[poz.razred,])
}
Specificity <- function(prave, napovedane, poz.razred)
{
	t <- table(prave, napovedane)

	#poiscemo index negativnega razreda
	neg.razred <- which(row.names(t) != poz.razred)

	t[neg.razred, neg.razred] / sum(t[neg.razred,])
}
# Funkcija za izracun Brierjeve mere
brier.score <- function(observedMatrix, predictedMatrix)
{
	sum((observedMatrix - predictedMatrix) ^ 2) / nrow(predictedMatrix)
}

#precno preverjanje
mypredict <- function(object, newdata){predict(object, newdata, type = "class")}
scale.data <- function(data)
{
	norm.data <- data

	for (i in 1:ncol(data))
	{
		if (!is.factor(data[,i]))
			norm.data[,i] <- scale(data[,i])
	}
	
	norm.data
}
wrapperReg <- function(dataset, regName, folds = 10)
{
	require(ipred)
	require(CORElearn)

	mypredict.regtree <- function(object, newdata) {pred <- predict(object, newdata); destroyModels(object); pred}
	mymodel.regtree <- function(formula, data) {CoreModel(formula, data, model = "regTree")}


	formula <- paste(regName," ~ ", sep = "")
	candidates <- names(dataset)[-match(regName,names(dataset))]

	global.res <- Inf
	global.formula <- formula

	while(length(candidates))
	{
		best.att <- 1
		best.res <- Inf

		for (i in 1:length(candidates))
		{
			tmp.formula <- paste(formula, candidates[i], sep = "")
			cat("ocenjujem ", tmp.formula, "...\n")
			flush.console()

			res <- errorest(as.formula(tmp.formula), data = dataset, model = mymodel.regtree, predict = mypredict.regtree, est.para=control.errorest(k=folds))
			if (res$error < best.res)
			{
				best.res <- res$error
				best.att <- i
			}
		}

		cat("izbral sem atribut ", candidates[best.att], "\n")

		flush.console()
		
		if (best.res < global.res)
		{
			global.formula <- paste(formula, candidates[best.att], sep = "")
			global.res <- best.res
		}
		
		formula <- paste(formula, candidates[best.att], " + ", sep = "")
		candidates <- candidates[-best.att]
	}

	cat("najbolje ocenjen model je ", global.formula, " z napako ", global.res, "\n")
}
wrapper <- function(dataset, className, mode,folds = 10)
{
	require(ipred)
	require(CORElearn)
	
	if(mode==1)
	{
		mymodel <- function(formula, data, target.model){CoreModel(formula, data, model="tree")}
		mypredict <- function(object, newdata) {pred <- predict(object, newdata)$class; destroyModels(object); pred}
	}
	if(mode==2)
	{
		mypredict.nb <- function(object, newdata) {pred <- predict(object, newdata)$class; destroyModels(object); pred}
		mymodel.nb <- function(formula, data) {CoreModel(formula, data, model = "bayes")}
	}
	
	
	formula <- paste(className," ~ ", sep = "")
	candidates <- names(dataset)[-match(className,names(dataset))]

	global.res <- 1.0
	global.formula <- formula

	while(length(candidates))
	{
		best.att <- 1
		best.res <- 1.0

		for (i in 1:length(candidates))
		{
			tmp.formula <- paste(formula, candidates[i], sep = "")
			cat("formula to evaluate:", tmp.formula, "...\n")
			flush.console()

			if(mode==1)
			{
				res <- errorest(as.formula(tmp.formula), data = dataset, model = mymodel, predict = mypredict, est.para=control.errorest(k=folds))
			}
			if(mode==2)
			{
				res <- errorest(as.formula(tmp.formula), data = dataset, model = mymodel.nb, predict = mypredict.nb, est.para=control.errorest(k=folds))
			}


			if (res$error < best.res)
			{
				best.res <- res$error
				best.att <- i
			}
		}

		cat("selected attribute: ", candidates[best.att], "\n")

		flush.console()
		
		if (best.res < global.res)
		{
			global.formula <- paste(formula, candidates[best.att], sep = "")
			global.res <- best.res
		}
		
		formula <- paste(formula, candidates[best.att], " + ", sep = "")
		candidates <- candidates[-best.att]
	}

	cat("best model: estimated error = ", global.res,", selected feature subset = ", global.formula, "\n")
}
#predelava podatkov
predelaj.razmerja <- function(inputData)
{
	result = zracunaj.zamenjaj.kosiRazmerja (inputData,"AWAY_2PA","AWAY_2PM")	
	result = zracunaj.zamenjaj.kosiRazmerja (result ,"AWAY_3PA","AWAY_3PM")
	result = zracunaj.zamenjaj.kosiRazmerja (result ,"AWAY_FTA","AWAY_FTM")	
	result = zracunaj.zamenjaj.kosiRazmerja (result ,"HOME_FTA","HOME_FTM")
	result = zracunaj.zamenjaj.kosiRazmerja (result,"HOME_2PA","HOME_2PM")	
	result = zracunaj.zamenjaj.kosiRazmerja (result ,"HOME_3PA","HOME_3PM")

	result = zracunaj.zamenjaj.skoki(result)

	result 
}
zracunaj.zamenjaj.kosiRazmerja <- function(inputData, prvo.ime, drugo.ime)
{
	which(colnames(inputData) == prvo.ime)
	A.index = which(colnames(inputData) == prvo.ime)
	M.index = which(colnames(inputData) == drugo.ime)

	for (i in 1:nrow(inputData))
	{
		A.value = inputData[i,A.index]
		M.value = inputData[i,M.index]

		twop.value = M.value/A.value

		inputData[i,A.index] = twop.value
	}

	inputData = inputData[,-M.index]

	inputData 
}
zracunaj.zamenjaj.skoki <- function(inputData)
{
	for (i in 1:nrow(inputData))
	{
		inputData[i,"HOME_ORB"] = inputData[i,"HOME_ORB"] / (inputData[i,"HOME_ORB"] +inputData[i,"AWAY_DRB"])
		inputData[i,"HOME_DRB"] = inputData[i,"HOME_DRB"] / (inputData[i,"HOME_DRB"] +inputData[i,"AWAY_ORB"])

		inputData[i,"AWAY_ORB"] = inputData[i,"AWAY_ORB"] / (inputData[i,"AWAY_ORB"] +inputData[i,"HOME_DRB"])
		inputData[i,"AWAY_DRB"] = inputData[i,"AWAY_DRB"] / (inputData[i,"AWAY_DRB"] +inputData[i,"HOME_ORB"])
	}
	inputData
}
poracunaj <- function()
{
majority.class <- names(which.max(table(train$WINNER)))
print("vecinska verjetnost:")
print(sum(train$WINNER== majority.class) / length(train$WINNER))

# drevo
print("drevo")
dt <- rpart(WINNER ~ ., data = learn)
observed <- test$WINNER
predicted <- predict(dt, test, type = "class")
print(CA(observed, predicted))
print(Sensitivity(observed, predicted, "A"))
print(Specificity(observed, predicted, "A"))

predMat <- predict(dt, test, type = "prob")
obsMat <- model.matrix(~WINNER-1, test)
print(brier.score(obsMat, predMat))



#naivni bayes
print("naivni bayes")
nb <- naiveBayes(WINNER  ~ ., data = learn)
observed <- test$WINNER
predicted <- predict(nb, test, type="class")

print(CA(observed, predicted))
print(Sensitivity(observed, predicted, "A"))
print(Specificity(observed, predicted, "A"))

predMat <- predict(nb, test, type = "raw")
obsMat <- model.matrix(~WINNER-1, test)
print(brier.score(obsMat, predMat))



#k-nn
print("knn")
cm.knn <- CoreModel(WINNER ~ ., data = learn, model="knn", kInNN = 100)
observed <- test$WINNER
predicted <- predict(cm.knn, test, type="class")

print(CA(observed, predicted))
print(Sensitivity(observed, predicted, "A"))
print(Specificity(observed, predicted, "A"))

predMat <- predict(cm.knn, test, type = "prob")
obsMat <- model.matrix(~WINNER-1, test)
print(brier.score(obsMat, predMat))


#nakljucni gozd
print("gozd")
rf <- randomForest(WINNER ~ ., data = learn)
observed <- test$WINNER
predicted <- predict(rf, test, type="class")
print(CA(observed, predicted))
print(Sensitivity(observed, predicted, "A"))
print(Specificity(observed, predicted, "A"))

predMat <- predict(rf, test, type = "prob")
obsMat <- model.matrix(~WINNER-1, test)
print(brier.score(obsMat, predMat))
#svm
print("svn")
sm <- svm(WINNER ~ ., data = learn)
observed <- test$WINNER
predicted <- predict(sm, test, type="class")
print(CA(observed, predicted))
print(Sensitivity(observed, predicted, "A"))
print(Specificity(observed, predicted, "A"))
#nn
print("nn")
norm.data <- scale.data(rbind(learn,test))
norm.learn <- norm.data[1:nrow(learn),]
norm.test <- norm.data[-(1:nrow(learn)),]
nn <- nnet(WINNER ~ ., data = norm.learn, size =1, decay = 0.0001, maxit = 1000)
predicted <- predict(nn, norm.test, type = "class")
print(CA(observed, predicted))
print(Sensitivity(observed, predicted, "A"))
print(Specificity(observed, predicted, "A"))
}

