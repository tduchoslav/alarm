import React, { Component } from 'react';
 import {SERVICE_BASE_URL, STATUS_RELATIVE_URL, ALARM_ENABLE_RELATIVE_URL, ALARM_DISABLE_RELATIVE_URL} from './AlarmConstants.jsx';
import AlarmStatusCheckbox from './AlarmStatusCheckbox.jsx';

/**
 * Component Alarm Status with checkbox on/off 
 */
class AlarmStatus extends Component {
    constructor(props) {
        super(props);
        this.state = {
                isAlarmOn : false,
                ajaxCallResult: true
        }
        
        this.ajaxGetAlarmStatus = this.ajaxGetAlarmStatus.bind(this);
        this.ajaxAlarmToggleOnOff = this.ajaxAlarmToggleOnOff.bind(this);
        this.handleStatusCheckboxChange = this.handleStatusCheckboxChange.bind(this);
        this.fetchStatusRequest = this.fetchStatusRequest.bind(this);
    }
    
    componentDidMount() {
        console.debug('AlarmStatus mount.');
        this.ajaxGetAlarmStatus();        
    }
    
    render() {
        console.debug('AlarmStatus render with status: %s', this.state.isAlarmOn);
        return (
                
                    <tr>
                        <td>
                            <span>Alarm:</span>
                        </td>                        
                        <td>
                            <AlarmStatusCheckbox on={this.state.isAlarmOn} handleStatusCheckboxChange={this.handleStatusCheckboxChange}/>
                        </td>
                        <td className='App-table-column-status-title'>
                            <span>
                                {this.state.isAlarmOn ? 'ON' : 'OFF'}
                            </span>
                        </td>
                        <td>
                            <span className = {this.state.ajaxCallResult ? 'App-rest-service-result-success' : 'App-rest-service-result-error'}>
                                {this.state.ajaxCallResult ? 'success' : 'failure'}
                            </span>
                        </td>
                    </tr>
                            
                );
    }
    
    handleStatusCheckboxChange(e) {
        e.preventDefault();
        let newState = !this.state.isAlarmOn;
        this.ajaxAlarmToggleOnOff(newState);
    }
    
    ajaxGetAlarmStatus() {
        let url = SERVICE_BASE_URL + STATUS_RELATIVE_URL;
        let request = new Request(url, {
            headers: new Headers({
                'Content-Type': 'text/plain',
                'accept': 'application/json'   
            })
        });
        this.fetchStatusRequest(request);
    }
    
    ajaxAlarmToggleOnOff(status) {
        let url = SERVICE_BASE_URL;
        if (status) {
            url = SERVICE_BASE_URL + ALARM_ENABLE_RELATIVE_URL;
        } else {
            url = SERVICE_BASE_URL + ALARM_DISABLE_RELATIVE_URL;
        }
        let request = new Request(url);
        this.fetchStatusRequest(request);
    }
    
    fetchStatusRequest(request) {        
        fetch(request)
        .then(results => {
            let json = results.json();
            console.info('results:  %s', json);
            return json;
            })
        .then(jsonData => 
            {
                this.setState({
                    isAlarmOn : jsonData['enabled'],
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

export default AlarmStatus;