import React, { Component } from 'react';
import {SERVICE_BASE_URL, ALARM_LAST_HEARTBEAT_RELATIVE_URL, ALARM_LAST_STATUS_RELATIVE_URL} from './AlarmConstants.jsx';
import AlarmLastTimestamp from './AlarmLastTimestamp.jsx';

class AlarmLastHeartBeatTimestamp extends Component {    
    constructor(props) {
        super(props);
        this.state = {
                timestamp: null,
                ajaxCallResult: true
        }
        
    }
    
    
    render() {
        console.debug('AlarmLastHeartBeatTimestamp render');

        return (
                <AlarmLastTimestamp timestamp = {this.state.timestamp} ajaxCallResult = {this.state.ajaxCallResult} title='HeartBeat Timestamp' />
                );
    }
    
    componentDidMount() {
        console.debug('AlarmLastHeartBeatTimestamp mount.');
        let url = SERVICE_BASE_URL + ALARM_LAST_HEARTBEAT_RELATIVE_URL;
        let request = new Request(url, {
            headers: new Headers({
                'Content-Type': 'text/plain',
                'accept': 'application/json'   
            })
        });
        fetch(request)
        .then(results => {
            let json = results.json();
            console.debug('results:  %s', json);
            return json;
            })
        .then(jsonData => 
            {
                this.setState({
                    timestamp : 'todo',
                    ajaxCallResult : true
                });
            })
        .catch(
                e => {
                        console.error('my error occured during fetching: %s got error %', request, e);
                        this.setState({ajaxCallResult : false});
                    }                
               );
    }
}


export default AlarmLastHeartBeatTimestamp;