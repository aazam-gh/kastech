#
# Copyright (c) 2023 Airbyte, Inc., all rights reserved.
#


from typing import Any, Iterable, Mapping

from airbyte_cdk import AirbyteLogger
from airbyte_cdk.destinations import Destination
from airbyte_cdk.models import AirbyteConnectionStatus, AirbyteMessage, ConfiguredAirbyteCatalog, Status
from requests_oauthlib import OAuth2Session
from requests.auth import HTTPBasicAuth
import requests
import base64

class DestinationQuickbooks(Destination):
    def write(
        self, config: Mapping[str, Any], configured_catalog: ConfiguredAirbyteCatalog, input_messages: Iterable[AirbyteMessage]
    ) -> Iterable[AirbyteMessage]:
        
        refresh_token = config['refresh_token']
        access_token = config['access_token']
        realm_id = config['realm_id']
        headers = {
        "Authorization": f"Bearer {access_token}",
        "Content-Type": "application/json",
        "Accept": "*/*",
        }
        datas = {
                "grant_type": "refresh_token",
                "refresh_token": refresh_token,
            }


        for message in input_messages:
            if message.type == type.RECORD:
                data = message.record.data

                endpoint = config.get('endpoint')
                if endpoint:
                    option_title = endpoint.get('option_title')

                sandbox_url = f"https://sandbox-quickbooks.api.intuit.com/v3/company/{realm_id}/customer"
                payload = data

                response = requests.post(sandbox_url, headers=headers, data=payload)
        pass

    def check(self, logger: AirbyteLogger, config: Mapping[str, Any]) -> AirbyteConnectionStatus:
        try:

            client_id = config['client_id']
            client_secret = config['client_secret']
            refresh_token = config['refresh_token']
            access_token = config['access_token']
            realm_id = config['realm_id']
            
            authorization_base_url = 'https://appcenter.intuit.com/connect/oauth2'
            token_url = 'https://oauth.platform.intuit.com/oauth2/v1/tokens/bearer'
            scope = ['com.intuit.quickbooks.accounting']

            sandbox_url = f"https://sandbox-quickbooks.api.intuit.com/v3/company/{realm_id}/companyinfo/{realm_id}"

            headers = {
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json",
            "Accept": "*/*",
            }
            datas = {
                "grant_type": "refresh_token",
                "refresh_token": refresh_token,
            }

            
            response = requests.get(sandbox_url, headers=headers)
            print(response.json)
            print(response.text)

            #response = requests.post(token_url, headers=headers, data=data)

            return AirbyteConnectionStatus(status=Status.SUCCEEDED)
        except Exception as e:
            return AirbyteConnectionStatus(status=Status.FAILED, message=f"An exception occurred: {repr(e)}")
