import subprocess

def noRowsCleared(W):
    #subprocess.check_output(['javac','Playground.java'])
    noCleared =  subprocess.check_output(['java','-cp','/Users/wojciechdziwulski/Coding/intro-to-ai-project/src','Playground',str(W[0]),str(W[1]),str(W[2]),str(W[3]),str(W[4]),str(W[5]),str(W[6]),str(W[7]),str(W[8]),str(W[9]),str(W[10]),str(W[11]),str(W[12]),str(W[13]),str(W[14]),str(W[15]),str(W[16]),str(W[17]),str(W[18]),str(W[19]),str(W[20]),str(W[21]),str(W[22]),str(W[23]),str(W[24]),str(W[25]),str(W[26]),str(W[27]),str(W[28]),str(W[29]),str(W[30]),str(W[31]),str(W[32])])
    return -int(noCleared)/100.0


# Write a function like this called 'main'
def main(job_id, params):
  print 'Anything printed here will end up in the output directory for job #:', str(job_id)
  print params
  return noRowsCleared(params['W'])


# params = {'W':[-4.42, 1.25, -3.73, 1.05, 2.71, 2.28, -4.20, -4.93, -4.38, -2.80, 3.36, -1.58, 2.29, -2.17, 3.54, -2.10, 0.39, -1.32, -1.95, -1.06, -0.84, -0.49, -1.26, 0.82, 3.80, -1.33, -2.99, -1.53, 0.65, 0.26, -1.15, -2.39, -0.84]}
# print main(5,params)
