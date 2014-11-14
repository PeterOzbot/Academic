# atributi
# [1] "DATE"           "AWAY_NAME"      "HOME_NAME"      "AWAY_2PA"      
# [5] "AWAY_2PM"       "AWAY_3PA"       "AWAY_3PM"       "AWAY_FTA"      
# [9] "AWAY_FTM"       "AWAY_TO"        "AWAY_ORB"       "AWAY_DRB"      
#[13] "HOME_2PA"       "HOME_2PM"       "HOME_3PA"       "HOME_3PM"      
#[17] "HOME_FTA"       "HOME_FTM"       "HOME_TO"        "HOME_ORB"      
#[21] "HOME_DRB"       "AWAY_PTS_Q1"    "HOME_PTS_Q1"    "AWAY_PTS_Q2"   
#[25] "HOME_PTS_Q2"    "AWAY_PTS_Q3"    "HOME_PTS_Q3"    "AWAY_PTS_Q4"   
#[29] "HOME_PTS_Q4"    "AWAY_PTS_FINAL" "HOME_PTS_FINAL"

# directory
setwd("J:\\SOLA\\UI\\Seminarska")
# nalozimo za leto 08/09
originalData0809 <- read.table("nba0809.txt", sep=",", header=TRUE)

# imena ekip in stevilo iger
table(originalData0809$AWAY_NAME)
table(originalData0809$HOME_NAME)

# samo imena ekip urejena
length(sort(unique(originalData0809$AWAY_NAME)))
sort(unique(originalData0809$HOME_NAME))

# izgubljene zoge
paste("povprecje izgubljenih zog domacih: ",mean(originalData0809$HOME_TO))
paste("povprecje izgubljenih zog tujih: ",mean(originalData0809$AWAY_TO))


# stevilo metov proti kosu(2 in 3 tocke) glede na domace proti tujim

domaci.povprecje.metov = mean(originalData0809$HOME_2PA + originalData0809$HOME_3PA)
tuji.povprecje.metov = mean(originalData0809$AWAY_2PA + originalData0809$AWAY_3PA)

paste("povprecje metov domacih: ",domaci.povprecje.metov)
paste("povprecje metov tujih: ",tuji.povprecje.metov)

domaci.povprecje.zadetihMetov = mean(originalData0809$HOME_2PM + originalData0809$HOME_3PM)
tuji.povprecje.zadetihMetov = mean(originalData0809$AWAY_2PM + originalData0809$AWAY_3PM)

paste("povprecje zadetih metov domacih: ",domaci.povprecje.zadetihMetov )
paste("povprecje zadetih metov tujih: ",tuji.povprecje.zadetihMetov )

domaci.uspesnos.metov = domaci.povprecje.zadetihMetov/domaci.povprecje.metov 
paste("uspesnost domacih : ",domaci.uspesnos.metov *100,"%")

tuji.uspesnos.metov = tuji.povprecje.zadetihMetov/tuji.povprecje.metov 
paste("uspesnost tujih : ",tuji.uspesnos.metov *100,"%")

#gibanje rezultata s casom

#število košev

#povprecno

mean.away.q1 = mean(originalData0809$AWAY_PTS_Q1)
mean.home.q1 = mean(originalData0809$HOME_PTS_Q1)

mean.away.q2 = mean(originalData0809$AWAY_PTS_Q2)
mean.home.q2 = mean(originalData0809$HOME_PTS_Q2)

mean.away.q3 = mean(originalData0809$AWAY_PTS_Q3)
mean.home.q3 = mean(originalData0809$HOME_PTS_Q3)

mean.away.q4 = mean(originalData0809$AWAY_PTS_Q4)
mean.home.q4 = mean(originalData0809$HOME_PTS_Q4)


plot(c(mean.away.q1,mean.away.q2,mean.away.q3,mean.away.q4),type="l",col=1)

lines(c(mean.home.q1,mean.home.q2,mean.home.q3,mean.home.q4),col=2)

#tocke po cetrtinah

mean.away.q1 = mean(originalData0809$AWAY_PTS_Q1)
mean.home.q1 = mean(originalData0809$HOME_PTS_Q1)

mean.away.q2 = mean(originalData0809$AWAY_PTS_Q2-originalData0809$AWAY_PTS_Q1)
mean.home.q2 = mean(originalData0809$HOME_PTS_Q2-originalData0809$HOME_PTS_Q1)


mean.away.q3 = mean(originalData0809$AWAY_PTS_Q3-originalData0809$AWAY_PTS_Q2-originalData0809$AWAY_PTS_Q1)
mean.home.q3 = mean(originalData0809$HOME_PTS_Q3-originalData0809$HOME_PTS_Q2-originalData0809$HOME_PTS_Q1)

mean.away.q4 = mean(originalData0809$AWAY_PTS_Q4-originalData0809$AWAY_PTS_Q3-originalData0809$AWAY_PTS_Q2-originalData0809$AWAY_PTS_Q1)
mean.home.q4 = mean(originalData0809$HOME_PTS_Q4-originalData0809$HOME_PTS_Q3-originalData0809$HOME_PTS_Q2-originalData0809$HOME_PTS_Q1)


plot(c(mean.away.q1,mean.away.q2,mean.away.q3,mean.away.q4),type="l",col=1)

lines(c(mean.home.q1,mean.home.q2,mean.home.q3,mean.home.q4),col=2)

#vsi kosi
pts.away.q1 = originalData0809$AWAY_PTS_Q1
pts.home.q1 = originalData0809$HOME_PTS_Q1

pts.away.q2 = originalData0809$AWAY_PTS_Q2
pts.home.q2 = originalData0809$HOME_PTS_Q2

pts.away.q3 = originalData0809$AWAY_PTS_Q3
pts.home.q3 = originalData0809$HOME_PTS_Q3

pts.away.q4 = originalData0809$AWAY_PTS_Q4
pts.home.q4 = originalData0809$HOME_PTS_Q4


plot(c(pts.away.q1,pts.away.q2,pts.away.q3,pts.away.q4),col=1)

plot(c(pts.home.q1,pts.home.q2,pts.home.q3,pts.home.q4),col=2)

# zmage po ekipah

#izracun zmag in porazov posameznih ekip

imena.ekip = vector()
zmage.ekip = vector()
porazi.ekip = vector()

for (i in 1:nrow(originalData0809))
{
	domaciTocke = as.integer(originalData0809[i,"HOME_PTS_FINAL"])
	tujiTocke = as.integer(originalData0809[i,"AWAY_PTS_FINAL"])

	ime.domace = as.character(originalData0809[i,"HOME_NAME"])
	ime.tuji = as.character(originalData0809[i,"AWAY_NAME"])

	index.domaci = which(imena.ekip == ime.domace)
	index.tuji = which(imena.ekip == ime.tuji)

	if(length(index.domaci) == 0)
	{
		imena.ekip = c(imena.ekip,ime.domace)
		index.domaci = which(imena.ekip == ime.domace)
	}
	index.tuji = which(imena.ekip == ime.tuji )
	if(length(index.tuji) == 0)
	{
		imena.ekip = c(imena.ekip,ime.tuji)
		index.tuji = which(imena.ekip == ime.tuji)
	}

	if(domaciTocke > tujiTocke){
		if(is.na(zmage.ekip[index.domaci]))
		{
			zmage.ekip[index.domaci]=0
		}
		if(is.na(porazi.ekip[index.tuji]))
		{
			porazi.ekip[index.tuji]=0
		}

		zmage.ekip[index.domaci] = zmage.ekip[index.domaci]+1
		porazi.ekip[index.tuji] = porazi.ekip[index.tuji]+1
	}

	if(domaciTocke < tujiTocke){
		if(is.na(porazi.ekip[index.domaci]))
		{
			porazi.ekip[index.domaci]=0
		}
		if(is.na(zmage.ekip[index.tuji]))
		{
			zmage.ekip[index.tuji]=0
		}

		porazi.ekip[index.domaci] = porazi.ekip[index.domaci]+1
		zmage.ekip[index.tuji] = zmage.ekip[index.tuji]+1
	}
}

# izpis zmag in porazov

for (i in 1:length(imena.ekip))
{
	print(paste("Ekipa :",imena.ekip[i],"Zmag: ",zmage.ekip[i],"Porazov: ",porazi.ekip[i]))
}

najvec.zmag.index = which.max(zmage.ekip)
print(paste("ekipa z navec zmagami: ",imena.ekip[najvec.zmag.index], "Zmag: ",zmage.ekip[najvec.zmag.index]))
najvec.porazov.index = which.max(porazi.ekip)
print(paste("ekipa z navec porazi: ",imena.ekip[najvec.porazov.index], "Porazov: ",porazi.ekip[najvec.porazov.index]))

#izracun in izpis razmerja zmag/porazov

povprecja.ekip = vector()# x zmag proti porazom
for (i in 1:length(imena.ekip))
{
	povprecja.ekip[i] = zmage.ekip[i] / porazi.ekip[i]
}
plot(povprecja.ekip,type="h",xaxt='n',xlab="Ekipe",ylab="Zmage/Porazi")
axis(1,1:30,imena.ekip)
lines(povprecja.ekip,col=1,type="p")


#izris
plot(zmage.ekip,type="h",xaxt='n',xlab="Ekipe",ylab="Zmage")
axis(1,1:30,imena.ekip)
lines(porazi.ekip,col=2,type="p")



# uspešnost metov glede na ekipe

imena.ekip = vector()
vsi.meti.ekip = vector()
neuspešni.meti.ekip = vector()


for (i in 1:nrow(originalData0809))
{
	domaci.vsi.meti = as.numeric(originalData0809[i,"HOME_2PA"]) + as.numeric(originalData0809[i,"HOME_3PA"]) + as.numeric(originalData0809[i,"HOME_FTA"])
	domaci.neuspešni.meti = as.numeric(originalData0809[i,"HOME_2PM"]) + as.numeric(originalData0809[i,"HOME_3PM"])+ as.numeric(originalData0809[i,"HOME_FTM"])
	
	tuji.vsi.meti = as.numeric(originalData0809[i,"AWAY_2PA"]) + as.numeric(originalData0809[i,"AWAY_3PA"])+ as.numeric(originalData0809[i,"AWAY_FTA"])
	tuji.neuspešni.meti = as.numeric(originalData0809[i,"AWAY_2PM"]) + as.numeric(originalData0809[i,"AWAY_3PM"])+ as.numeric(originalData0809[i,"AWAY_FTM"])	

	ime.domace = as.character(originalData0809[i,"HOME_NAME"])
	ime.tuji = as.character(originalData0809[i,"AWAY_NAME"])

	index.domaci = which(imena.ekip == ime.domace)
	index.tuji = which(imena.ekip == ime.tuji)

	if(length(index.domaci) == 0)
	{
		imena.ekip = c(imena.ekip,ime.domace)
		index.domaci = which(imena.ekip == ime.domace)
	}
	index.tuji = which(imena.ekip == ime.tuji )
	if(length(index.tuji) == 0)
	{
		imena.ekip = c(imena.ekip,ime.tuji)
		index.tuji = which(imena.ekip == ime.tuji)
	}

	if(is.na(vsi.meti.ekip[index.tuji]))
		{
			vsi.meti.ekip[index.tuji]=0
		}
	if(is.na(neuspešni.meti.ekip[index.tuji]))
		{
			neuspešni.meti.ekip[index.tuji]=0
		}

	if(is.na(vsi.meti.ekip[index.domaci]))
		{
			vsi.meti.ekip[index.domaci]=0
		}
	if(is.na(neuspešni.meti.ekip[index.domaci]))
		{
			neuspešni.meti.ekip[index.domaci]=0
		}

	vsi.meti.ekip[index.tuji] = vsi.meti.ekip[index.tuji] + tuji.vsi.meti
	neuspešni.meti.ekip[index.tuji] = neuspešni.meti.ekip[index.tuji] + tuji.neuspešni.meti

	vsi.meti.ekip[index.domaci] = vsi.meti.ekip[index.domaci] + domaci.vsi.meti
	neuspešni.meti.ekip[index.domaci] = neuspešni.meti.ekip[index.domaci] + domaci.neuspešni.meti
}

#izracun razmerja
razmerja.meti.ekip = vector()

for (i in 1:length(imena.ekip))
{
	razmerja.meti.ekip[i] = neuspešni.meti.ekip[i] / vsi.meti.ekip[i]
}

for (i in 1:length(imena.ekip))
{
	print(paste("Ekipa :",imena.ekip[i],"Vsi meti: ",vsi.meti.ekip[i],"Uspešni meti: ",neuspešni.meti.ekip[i],"Razmerje: ",razmerja.meti.ekip[i]))
}

#izris razmerji za ekipe

plot(razmerja.meti.ekip,type="h",xaxt='n',xlab="Ekipe",ylab="Razmerje uspešnih/vseh")
axis(1,1:30,imena.ekip)































