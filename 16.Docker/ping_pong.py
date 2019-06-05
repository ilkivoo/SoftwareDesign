import sys
import boto3

endpoint_url = 'http://localstack:4576'
aws_access_key_id = 'aws_access_key_id'
aws_secret_access_key = 'aws_secret_access_key'

sqs = boto3.resource('sqs', endpoint_url=endpoint_url, aws_access_key_id=aws_access_key_id,
                     aws_secret_access_key=aws_secret_access_key)
if len(sys.argv) != 3:
    print("Expected queue_name_from and queue_name_to")
    exit(1)
queue_name_from = sys.argv[1]
queue_name_to = sys.argv[2]
while True:
    try:
        queue_from = sqs.get_queue_by_name(QueueName=queue_name_from)
        queue_to = sqs.get_queue_by_name(QueueName=queue_name_to)
        break
    except:
        print("Queue not created")
while True:
    for message in queue_from.receive_messages():
        new_num = str(int(message.body) + 1)
        print("Queue " + queue_name_from + " received " + message.body, flush=True)
        message.delete()
        queue_to.send_message(MessageBody=new_num)
