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
                isAlarmOn : false
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
                        <th>Alarm Status:</th>                        
                        <th>
                            <AlarmStatusCheckbox on={this.state.isAlarmOn} handleStatusCheckboxChange={this.handleStatusCheckboxChange}/>
                        </th>
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
                this.setState({isAlarmOn : jsonData['enabled']});
            })
        .catch(e => {console.error('my error occured during fetching: %s got error %', request, e)});
    }
}

export default AlarmStatus;