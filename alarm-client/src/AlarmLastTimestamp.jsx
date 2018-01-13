import React, { Component } from 'react';

class AlarmLastTimestamp extends Component {    
    constructor(props) {
        super(props);
    }
    
    
    render() {
        console.debug('AlarmLastTimestamp render');

        return (
                
                    <tr>
                        <td>
                            <span>{this.props.title}</span>
                        </td>                        
                        <td>
                        </td>
                        <td>
                            {this.props.timestamp}
                        </td>
                        <td>
                            <span className = {this.props.ajaxCallResult ? 'App-rest-service-result-success' : 'App-rest-service-result-error'}>
                            {this.props.ajaxCallResult ? 'success' : 'failure'}
                        </span>
                        </td>
                    </tr>
                            
                );
    }
    
}


export default AlarmLastTimestamp;