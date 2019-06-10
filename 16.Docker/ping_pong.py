import sys
import boto3

endpoint_url = 'http://localstack:4576'
aws_access_key_id = 'aws_access_key_id'
aws_secret_access_key = 'aws_secret_access_key'
region_name = 'us-west-2'
sqs = boto3.resource('sqs', endpoint_url=endpoint_url, aws_access_key_id=aws_access_key_id,
                     aws_secret_access_key=aws_secret_access_key, region_name=region_name)


def create_queue(name):
    while True:
        try:
            queue = sqs.create_queue(QueueName=name)
            print("Create queue with name=" + name)
            return queue
        except:
            print("Failed to create queue")


def get_queue(name):
    try:
        return sqs.get_queue_by_name(QueueName=name)
    except:
        return create_queue(name)


def init_message(queue, queue_name):
    request = "1"
    response = queue.send_message(MessageBody=request)
    print("Send request to " + queue_name + " " + request)


if len(sys.argv) != 3:
    print("Expected queue_name_from and queue_name_to")
    exit(1)
queue_name_from = sys.argv[1]
queue_name_to = sys.argv[2]

queue_from = get_queue(queue_name_from)
queue_to = get_queue(queue_name_to)
if queue_name_from == 'A':
    init_message(queue_from, queue_name_from)

while True:
    for receive_message in queue_from.receive_messages():
        request_num = int(receive_message.body)
        response_num = str(request_num + 1)
        print("Queue: request_num=" + str(request_num) + ", response_num = " + response_num, flush=True)
        receive_message.delete()
        queue_to.send_message(MessageBody=response_num)
