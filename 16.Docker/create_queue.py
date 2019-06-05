import sys
import boto3
from time import sleep

endpoint_url = 'http://localstack:4576'
aws_access_key_id = 'aws_access_key_id'
aws_secret_access_key = 'aws_secret_access_key'

sqs = boto3.resource('sqs', endpoint_url=endpoint_url, aws_access_key_id=aws_access_key_id,
                     aws_secret_access_key=aws_secret_access_key)


def create_queue(name):
    queue_created = False
    while not queue_created:
        try:
            queue = sqs.create_queue(QueueName=name, Attributes={'DelaySeconds': '3'})
            queue_created = True
            print("Queue " + name + " created successfully: " + queue.url)
        except:
            print("LocalStack haven't started yet. Waiting...")
            sleep(1)


for i in range(1, len(sys.argv)):
    while True:
        try:
            queue = sqs.create_queue(QueueName=sys.argv[i])
            print("Create queue with name=" + sys.argv[i])
            break
        except:
            print("Failed to create queue")
