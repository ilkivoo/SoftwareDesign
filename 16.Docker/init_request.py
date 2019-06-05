import sys
import boto3

endpoint_url = 'http://localstack:4576'
aws_access_key_id = 'aws_access_key_id'
aws_secret_access_key = 'aws_secret_access_key'
region_name = 'us-west-2'
sqs = boto3.resource('sqs', endpoint_url=endpoint_url, aws_access_key_id=aws_access_key_id,
                     aws_secret_access_key=aws_secret_access_key, region_name=region_name)

if len(sys.argv) != 3:
    print("Expected queue_name and message")
    exit(1)

queue_name = sys.argv[1]
request = sys.argv[2]
print(queue_name)
print(request)
while True:
    try:
        queue = sqs.get_queue_by_name(QueueName=queue_name)
        response = queue.send_message(MessageBody=request)
        print("Send request to " + queue_name + " " + request)
        break
    except:
        print("Queue not created " + queue_name)
