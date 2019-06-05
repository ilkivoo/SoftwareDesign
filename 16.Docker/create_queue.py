import sys
import boto3

endpoint_url = 'http://localstack:4576'
aws_access_key_id = 'aws_access_key_id'
aws_secret_access_key = 'aws_secret_access_key'

sqs = boto3.resource('sqs', endpoint_url=endpoint_url, aws_access_key_id=aws_access_key_id,
                     aws_secret_access_key=aws_secret_access_key)

for i in range(1, len(sys.argv)):
    while True:
        try:
            queue = sqs.create_queue(QueueName=sys.argv[i])
            print("Create queue with name=" + sys.argv[i])
            break
        except:
            print("Failed to create queue")
